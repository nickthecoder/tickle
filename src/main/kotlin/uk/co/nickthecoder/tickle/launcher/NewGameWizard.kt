/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.launcher

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.PrintErrSink
import uk.co.nickthecoder.paratask.util.process.PrintOutSink
import java.io.File

/**
 * Creates a new project (for a new game).
 * It creates the directory hierarchy including the files :
 *
 * - build.gradle
 * - README.md
 * - The resources file (file type .tickle)
 * - The Producer class (file type .kt). This also contains the game's 'main' entry point
 * - A scene (file type .scene), with no actors.
 * - .gitignore (if git option is chosen)
 *
 * It then optionally initialises git (including the first commit) and creates IntelliJ IDEA project files.
 * Finally the Tickle editor is launched.
 *
 * This uses a JavaFX GUI. Use [NewGameWizardApp] to launch.
 */
class NewGameWizard() : AbstractTask() {

    private val gameName = StringParameter("gameName", hint = "Only letters, numbers, spaces and underscores are allowed. e.g. Space Invaders", required = true)

    private val parentDirectory = FileParameter("parentDirectory", hint = "Do NOT include the name of the game (it will be added automatically)", mustExist = true, expectFile = false)

    private val initialSceneName = StringParameter("initialSceneName", required = true, value = "menu")

    private val width = IntParameter("width", value = 640)
    private val cross = LabelParameter("cross", "x")
    private val height = IntParameter("height", value = 480)

    private val size = SimpleGroupParameter("size")
            .addParameters(width, cross, height)
            .asHorizontal(LabelPosition.NONE)

    private val packageBase = StringParameter("packageBase", hint = "Blank, or your domain name backwards (e.g. com.example)", required = false)

    private val groovy = BooleanParameter("enableGroovyScripts", value = true)

    private val git = BooleanParameter("initialiseGit", value = true, hint = "If you don't know what git is, google it! It's very useful.")

    override val taskD = TaskDescription("New Game Wizard")
            .addParameters(gameName, parentDirectory, size, initialSceneName, packageBase, groovy, git)


    override fun check() {
        super.check()
        if (!gameName.value.matches(Regex("[a-zA-Z0-9 _]*"))) {
            throw ParameterException(gameName, "Only letters, numbers, spaces and underscores are allowed")
        }
        if (gameDirectory().exists()) {
            throw ParameterException(gameName, "Directory '${gameDirectory().name}' already exists")
        }
        if (!packageBase.value.matches(Regex("[a-zA-Z0-9.]*"))) {
            throw ParameterException(packageBase, "Only letters, numbers and periods allowed")
        }
    }

    fun identifier() = gameName.value.replace(Regex(" "), "")

    fun scripted() = groovy.value == true

    fun gameDirectory() = File(parentDirectory.value, identifier().toLowerCase())

    fun packageName(): String {
        return if (packageBase.value.isBlank())
            identifier().toLowerCase()
        else
            "${packageBase.value}.${identifier().toLowerCase()}"
    }

    fun mainPackageDir(): File {
        val kotlin = gameDirectory().child("src", "main", "kotlin")
        return File(kotlin, packageName().replace('.', File.separatorChar))
    }

    fun resourcesDir() = if (scripted()) gameDirectory() else gameDirectory().child("src", "dist", "resources")

    fun resourcesFile() = File(resourcesDir(), identifier() + ".tickle")

    override fun run() {
        val directory = gameDirectory()
        val packageDir = mainPackageDir()
        val resourcesDir = resourcesDir()
        val scripted = groovy.value == true

        println("Creating directory structure at : $directory")

        directory.mkdir()
        packageDir.mkdirs()
        resourcesDir.mkdirs()
        resourcesDir.child("scenes").mkdir()
        resourcesDir.child("images").mkdir()
        resourcesDir.child("sounds").mkdir()
        if (scripted) {
            File(resourcesDir, "scripts").mkdir()
        }

        val resourcesFile = resourcesFile()
        println("Creating $resourcesFile")
        resourcesFile.writeText(resourceContents())

        val mainSceneFile = resourcesDir.child("scenes", "menu.scene")
        println("Creating $mainSceneFile")
        mainSceneFile.writeText(mainSceneContents())

        if (!scripted) {
            val mainClassFile = File(packageDir, identifier() + ".kt")
            println("Creating $mainClassFile")
            mainClassFile.writeText(mainClassContents())

            val gradleFile = File(directory, "build.gradle")
            println("Creating $gradleFile")
            gradleFile.writeText(gradleContents())
        }

        val readmeFile = File(directory, "README.md")
        println("Creating $readmeFile")
        readmeFile.writeText(readMeContents())

        if (git.value == true) {
            val gitIgnoreFile = File(directory, ".gitignore")
            println("Creating $gitIgnoreFile")
            gitIgnoreFile.writeText(gitIgnoreContents())
            println("Initialising git")
            exec(directory, "git", "init")
            println("Performing first git commit")
            exec(directory, "git", "add", ".")
            exec(directory, "git", "commit", "-m", "Project created using NewGameWizard")
        }

        println("\nProject Created.\n")

        if (!scripted) {
            if (exec(directory, "gradle", "installDist")) {
                println("\n\nBuild OK\n")
            }
            println("\nTo build the game : ")
            println("cd '$directory'")
            println("gradle installDist")
            println("\nTo run the game : ")
            println("build/install/${identifier().toLowerCase()}/bin/${identifier().toLowerCase()}")
            println("\nTo launch the editor : ")
            println("build/install/${identifier().toLowerCase()}/bin/${identifier().toLowerCase()} --editor")
        }

    }

    fun exec(dir: File, program: String, vararg args: String): Boolean {
        println("Running $program ${args.joinToString(separator = " ")} (dir=$dir)")
        val cmd = OSCommand(program, * args)
        cmd.directory = dir
        val exec = Exec(cmd)
        exec.outSink = PrintOutSink()
        exec.errSink = PrintErrSink()
        val result = exec.start().waitFor(30) // Run, and abort if the command takes more than 30 seconds.
        if (result != 0) {
            println("Exit code : $result")
        }
        return result == 0
    }

    /*
    The following functions are templates for each of the files to be generated.
     */

    fun gradleContents() = """
buildscript {
    ext.kotlin_version = '1.1.3-2'

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${'$'}kotlin_version"
    }
}

apply plugin: 'kotlin'
apply plugin: 'idea'
apply plugin: 'application'

mainClassName = "${packageName()}.${identifier()}Kt"

defaultTasks 'installDist'

version = '0.1'
group = '${packageBase.value}'

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compile 'uk.co.nickthecoder:tickle-core:${tickleVersion}'
    compile 'uk.co.nickthecoder:tickle-editor:${tickleVersion}'
    //compile 'org.reflections:reflections:0.9.11'
    ${if (groovy.value == true) "compile 'uk.co.nickthecoder:tickle-groovy:${tickleVersion}'" else ""}
}

"""

    fun resourceContents() = """
{
  "info": {
    "title": "${gameName.value}",
    "width": ${width.value},
    "height": ${height.value},
    "initialScene": "${initialSceneName.value}",
    "testScene": "${initialSceneName.value}",
    "producer": "uk.co.nickthecoder.tickle.NoProducer"
  },
  "preferences": {
    "outputFormat": "PRETTY",
    "packages": [
      "uk.co.nickthecoder.tickle" ${if (scripted()) "" else "," + packageName()}
    ]
  },
  "layouts": [
    {
      "name": "default",
      "stages": [
        {
          "name": "main",
          "isDefault": true,
          "stage": "uk.co.nickthecoder.tickle.stage.GameStage",
          "constraint": "uk.co.nickthecoder.tickle.resources.NoStageConstraint"
        }
      ],
      "views": [
        {
          "name": "main",
          "view": "uk.co.nickthecoder.tickle.stage.ZOrderStageView",
          "stage": "main",
          "zOrder": 50,
          "hAlignment": "LEFT",
          "leftRightMargin": 0,
          "vAlignment": "TOP",
          "topBottomMargin": 0
        }
      ]
    }
  ]
}
"""

    fun mainClassContents() = """
package ${packageName()}

import uk.co.nickthecoder.tickle.AbstractProducer
import uk.co.nickthecoder.tickle.editor.EditorMain
${if (groovy.value == true) "import uk.co.nickthecoder.tickle.groovy.GroovyLanguage" else ""}

/**
 * The main entry point for the game.
 */
fun main(args: Array<String>) {
    ${if (groovy.value == true) "GroovyLanguage().register()" else ""}
    EditorMain("${identifier().toLowerCase()}", args).start()
}

"""

    fun mainSceneContents() = """
{
  "director": "uk.co.nickthecoder.tickle.NoDirector",
  "background": "#000000",
  "showMouse": true,
  "layout": "default",
  "stages": [
    {
      "name": "main",
      "actors": [
      ]
    }
  ]
}
"""

    fun gitIgnoreContents() = """
/.gradle
/.idea
/build
/gradle
/gradlew
/gradlew.bat
/out
/${identifier().toLowerCase()}.iml
/${identifier().toLowerCase()}.ipr
/${identifier().toLowerCase()}.iws
"""


    fun readMeContents() = "# ${gameName.value}" + if (scripted()) """

""" else """

To build the game :

    gradle installDist

To run the game :

    build/install/foo/bin/foo

To launch the editor :

    build/install/foo/bin/foo --editor

To create a zip file, ready for distribution :

    gradle distZip

""" + "Powered by [Tickle](https://github.com/nickthecoder/tickle) and [LWJGL](https://www.lwjgl.org/)."

}

private var tickleVersion = "0.1"

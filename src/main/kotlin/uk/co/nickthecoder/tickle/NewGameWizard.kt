package uk.co.nickthecoder.tickle

import javafx.application.Application
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.child
import java.io.File

/**
 * Creates a new project (for a new game).
 * It creates the directory hierarchy as well as four files :
 *
 * - The build.gradle file
 * - The resource file (file type .tickle)
 * - The Producer class (file type .kt). This also contains the game's 'main' entry point
 * - A scene (file type .scene), with no actors.
 *
 */
class NewGameWizard : AbstractTask() {

    override val taskD = TaskDescription("New Game Wizard")

    val gameName = StringParameter("gameName", required = true)

    val parentDirectory = FileParameter("parentDirectory", mustExist = true, expectFile = false)

    val packagePrefix = StringParameter("packagePrefix", required = false)

    val initialSceneName = StringParameter("initialSceneName", required = true, value = "menu")

    val width = IntParameter("width", value = 640)
    val height = IntParameter("height", value = 480)

    init {
        taskD.addParameters(gameName, parentDirectory, packagePrefix, width, height, initialSceneName)
    }

    override fun check() {
        super.check()
        if (!gameName.value.matches(Regex("[a-zA-Z0-9 _]*"))) {
            throw ParameterException(gameName, "Only alpha numeric, space and underscore characters allowed")
        }
        if (gameDirectory().exists()) {
            throw ParameterException(gameName, "Directory '${gameDirectory().name}' already exists")
        }
        if (!packagePrefix.value.matches(Regex("[a-zA-Z0-9.]*"))) {
            throw ParameterException(packagePrefix, "Only letters, numbers and periods allowed")
        }
    }

    fun identifier() = gameName.value.replace(Regex(" "), "")

    fun gameDirectory() = File(parentDirectory.value, identifier().toLowerCase())

    fun packageName(): String {
        return if (packagePrefix.value.isBlank())
            identifier().toLowerCase()
        else
            "${packagePrefix.value}.${identifier().toLowerCase()}"
    }

    fun mainPackageDir(): File {
        val kotlin = gameDirectory().child("src", "main", "kotlin")
        return File(kotlin, packageName().replace('.', File.separatorChar))
    }

    override fun run() {
        val id = identifier()
        val directory = gameDirectory()
        val packageDir = mainPackageDir()
        val resourcesDir = directory.child("src", "dist", "resources")

        println("Creating directory structure at : ${directory}")

        directory.mkdir()
        packageDir.mkdirs()
        resourcesDir.mkdirs()
        resourcesDir.child("scenes").mkdir()
        resourcesDir.child("images").mkdir()
        resourcesDir.child("sounds").mkdir()

        val resourcesFile = File(resourcesDir, id + ".tickle")
        println("Creating $resourcesFile")
        resourcesFile.writeText(resourceContents())

        val mainClassFile = File(packageDir, identifier() + ".kt")
        println("Creating $mainClassFile")
        mainClassFile.writeText(mainClassContents())

        val mainSceneFile = resourcesDir.child("scenes", "menu.scene")
        println("Creating $mainSceneFile")
        mainSceneFile.writeText(mainSceneContents())

        val gradleFile = File(directory, "build.gradle")
        println("Creating $gradleFile")
        gradleFile.writeText(gradleContents())

        println("\nProject Created.\n")

        println("To build the game, : ")
        println("cd '$directory'")
        println("gradle installApp")
        println("\nTo run the game : ")
        println("build/install/${identifier().toLowerCase()}/bin/${identifier().toLowerCase()}")
        println("\nTo lauch the editor : ")
        println("build/install/${identifier().toLowerCase()}/bin/${identifier().toLowerCase()} --editor")
        println("To create an Intellij IDEA project for the game : ")
        println("gradle idea")
    }

    fun gradleContents() = """
project.ext.lwjglVersion = "3.1.3"
project.ext.jomlVersion = "1.9.4"

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

defaultTasks 'installApp'

version = '0.1'
group = '${packagePrefix.value}'

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compile 'uk.co.nickthecoder:tickle:0.1'
    compile 'org.reflections:reflections:0.9.11'
}

compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
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
    "producer": "${packageName()}.${identifier()}"
  },
  "preferences": {
    "outputFormat": "PRETTY",
    "packages": [
      "uk.co.nickthecoder.tickle",
      "${packageName()}"
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
import uk.co.nickthecoder.tickle.namedMain

/**
 * The main entry point for the game.
 * See [namedMain] for usage information.
 */
fun main(args: Array<String>) {
    namedMain("${identifier().toLowerCase()}", args)
}

class ${identifier()} : AbstractProducer()
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

}

class NewGameWizardApp : Application() {

    override fun start(stage: Stage?) {
        TaskPrompter(NewGameWizard()).placeOnStage(stage ?: Stage())
    }
}

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
package uk.co.nickthecoder.tickle.metadata

import com.google.common.reflect.ClassPath
import uk.co.nickthecoder.tickle.Role
import java.io.File

/**
 * Builds a object containing meta-data about the classes within tickle-core.
 * The built object  is compiled into the tickle-editor jar, to help CodeEditor
 * perform auto-completion and auto-import.
 */
fun main(vararg args: String) {

    val projectDir = if (File("").absoluteFile.name == "metadata") File("..") else File(".")
    val file = File(projectDir, "tickle-editor/src/main/kotlin/uk/co/nickthecoder/tickle/editor/util/ClassMetaData.kt")
    val out = file.printWriter()

    out.println("""
package uk.co.nickthecoder.tickle.editor.util

/**
 * Generated by uk.co.nickthecoder.tickle.metadata.MetaData
 *
 * Do NOT edit this! Instead, re-run the :metadata project.
 */
object ClassMetaData {
""")

    val packages = listOf("uk.co.nickthecoder.tickle", "org.joml")

    val simpleClassNameToNames = mutableMapOf<String, MutableList<String>>()

    val cp = ClassPath.from(Role::class.java.classLoader)
    for (pack in packages) {
        val classes = cp.getTopLevelClassesRecursive(pack)
        classes.forEach { info ->
            val existingList = simpleClassNameToNames[info.simpleName]
            if (existingList == null) {
                simpleClassNameToNames[info.simpleName] = mutableListOf(info.name)
            } else {
                existingList.add(info.name)
            }
        }
    }

    out.println("    val simpleClassNameToNames = mapOf<String,List<String>>(")
    out.print("        ")
    out.println(simpleClassNameToNames.toList().sortedBy { it.first }.joinToString(separator = ",\n        ") { (simpleName, names) ->
        val list = names.joinToString(separator = ", ") { "\"$it\"" }
        "\"$simpleName\" to listOf( $list )"
    })
    out.println("    )")

    out.println("}")

    out.close()
}

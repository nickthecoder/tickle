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
package uk.co.nickthecoder.tickle.scripts

import java.io.File

/**
 * Holds the set of script languages used by your game.
 * Note, this only supports languages that can be dynamically compiled to JVM classes.
 * So, for example, it does not support interpreted javascript.
 *
 * If your game only use compiled Kotlin/Java, then there is no need for ScriptManager.
 */
object ScriptManager {

    private val languages = mutableMapOf<String, Language>()

    fun register(language: Language) {
        println("Registering language ${language.fileExtension}")
        languages[language.fileExtension] = language
    }

    fun addPath(directory: File) {
        languages.values.forEach { it.addPath(directory) }
        scan(directory)
    }

    fun scan(directory: File) {
        println("Scanning directory $directory")
        directory.listFiles().forEach { file ->
            val ext = file.extension
            languages[ext]?.let { language ->
                val klass = language.addScript(file)
            }
        }
    }

    fun subTypes(type: Class<*>): List<Class<*>> {
        val results = mutableListOf<Class<*>>()

        languages.values.forEach { language ->
            results.addAll(language.subTypes(type))
        }
        return results
    }

    fun classForName(className: String): Class<*> {
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            languages.values.forEach {
                val klass = it.classForName(className)
                if (klass != null) {
                    return klass
                }
            }
        }
        throw ClassNotFoundException(className)
    }
}


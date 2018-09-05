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

    private lateinit var classpath: File

    fun languages() = languages.values

    fun register(language: Language) {
        println("Registering language ${language.fileExtension}")
        languages[language.fileExtension] = language
    }

    /**
     * Note, only one directory is currently supported. All script class must be only
     * within that directory.
     * Also, packages are NOT supported, so there are no subdirectories.
     */
    fun setClasspath(directory: File) {
        classpath = directory
        scan()
    }

    fun scan() {
        languages.values.forEach { it.setClasspath(classpath) }
        if (languages.isNotEmpty()) {
            println("Scanning script directory $classpath")
            classpath.listFiles()?.forEach { file ->
                load(file)
            }
        }
    }

    fun reloadAll() {
        languages.values.forEach { it.clear() }
        scan()
    }

    fun load(file: File) {
        languages[file.extension]?.addScript(file)
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

    fun nameForClass(klass: Class<*>): String {
        for (language in languages.values) {
            language.nameForClass(klass)?.let { return it }
        }
        return klass.name
    }
}


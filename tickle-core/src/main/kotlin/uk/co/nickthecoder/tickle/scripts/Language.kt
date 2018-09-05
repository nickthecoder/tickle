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
import java.lang.ref.WeakReference
import java.lang.reflect.Modifier

/**
 * The base class for scripting languages, allowing game code to be dynamically loaded.
 *
 * Note, you game's main entry point should register the script language(s) you require e.g. :
 *
 *    GroovyLanguage().register()
 *
 * Also, you must add the path(s) where script files are located.
 * However, the default launchers: Tickle and EditorMain will do the following automatically :
 *
 *    ScriptManager.setClasspath(File(resourcesFile.parent, "scripts"))
 */
abstract class Language {

    abstract val fileExtension: String

    abstract val name: String

    /**
     * Key is the name of the filename without extension.
     * Value is the Class contained in that file.
     * Note. the filename is used, rather than the Class.name, because Kotlin creates classes with names like :
     * Line_3$ExampleRole. Grr.
     */
    private val classes = mutableMapOf<String, Class<*>>()

    /**
     * Note, when a script is reloaded, this map will contain the old and new classes,
     * and therefore will grow larger than [classes] map.
     * A WeakReference is used so that old (unused) classes can be garbage collected.
     */
    private val classToName = mutableMapOf<WeakReference<Class<*>>, String>()

    open fun register() {
        ScriptManager.register(this)
    }

    abstract fun setClasspath(directory: File)

    /**
     * Clear all classes, ready for them to be reloaded
     */
    open fun clear() {
        classes.clear()
        classToName.clear()
    }

    abstract fun loadScript(file: File): Class<*>

    fun addScript(file: File) {
        try {
            val oldClass = classes[name]
            val klass = loadScript(file)
            val name = file.nameWithoutExtension
            classes[name] = klass
            classToName[WeakReference(klass)] = name
        } catch (e: Exception) { // TODO Change to a ScriptException, and handle the error, sending events to listeners
            System.err.println("Failed to load script $file :\n$e")
        }
    }

    fun classForName(name: String): Class<*>? {
        return classes[name]
    }

    fun nameForClass(klass: Class<*>): String? {
        for ((weakKlass, name) in classToName) {
            if (weakKlass.get() === klass) {
                return name
            }
        }
        return null
    }

    /**
     * Returns classes known by this script language, that are sub-classes of the type given.
     * For example, it can be used to find all the scripted Roles, or scripted Directors etc.
     */
    fun subTypes(type: Class<*>): List<Class<*>> {
        return classes.filter {
            !it.value.isInterface && !Modifier.isAbstract(it.value.modifiers) && type.isAssignableFrom(it.value)
        }.toSortedMap().map { it.value }
    }

    fun createScript(scriptDirectory: File, scriptName: String, type: Class<*>? = null): File {
        val file = File(scriptDirectory, scriptName + ".${fileExtension}")
        if (!scriptDirectory.exists()) {
            scriptDirectory.mkdirs()
        }
        file.writeText(generateScript(scriptName, type))
        ScriptManager.load(file)
        return file
    }

    abstract fun generateScript(name: String, type: Class<*>?): String
}

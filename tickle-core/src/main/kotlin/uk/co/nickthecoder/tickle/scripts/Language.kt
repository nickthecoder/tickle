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
 *    ScriptManager.addPath(File(resourcesFile.parent, "scripts"))
 */
abstract class Language {

    abstract val fileExtension: String

    val classes = mutableMapOf<String, Class<*>>()

    val classToName = mutableMapOf<WeakReference<Class<*>>, String>()

    open fun register() {
        ScriptManager.register(this)
    }

    abstract fun addPath(directory: File)

    abstract fun loadScript(file: File): Class<*>

    fun addScript(file: File) {
        val klass = loadScript(file)
        classes[klass.name] = klass
        classToName[WeakReference(klass)] = file.name
    }

    fun nameOf(klass: Class<*>): String? {
        return classToName.filter { it.key.get() === klass }.map { it.value }.firstOrNull()
    }

    fun classForName(className: String): Class<*>? {
        return classes[className]
    }

    /**
     * Returns classes known by this script language, that are sub-classes of the type given.
     * For example, it can be used to find all the scripted Roles, or scripted Directors etc.
     */
    fun subTypes(type: Class<*>): List<Class<*>> {
        return classes.values.filter { !it.isInterface && !Modifier.isAbstract(it.modifiers) && type.isAssignableFrom(it) }
                .sortedBy { it.name }
    }
}

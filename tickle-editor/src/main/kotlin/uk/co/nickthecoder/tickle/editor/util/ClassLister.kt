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
package uk.co.nickthecoder.tickle.editor.util

import org.reflections.Reflections
import uk.co.nickthecoder.paratask.parameters.GroupedChoiceParameter
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import java.lang.reflect.Modifier


object ClassLister {

    var reflectionsMap = mutableMapOf<String, Reflections>()

    private val cache = mutableMapOf<Class<*>, List<Class<*>>>()

    init {
        addPackage("uk.co.nickthecoder.tickle")
    }

    fun packages(packages: List<String>) {
        reflectionsMap.clear()
        packages.forEach { addPackage(it) }
    }

    fun addPackage(pack: String) {
        cache.clear()
        reflectionsMap[pack] = Reflections(pack)
    }

    fun subTypes(type: Class<*>): List<Class<*>> {
        val cached = cache[type]
        if (cached != null) {
            return cached
        }

        val results = mutableListOf<Class<*>>()

        // Find all compiled classes of the given type. i.e. NOT scripted languages.
        reflectionsMap.values.forEach {
            results.addAll(it.getSubTypesOf(type).filter { !it.isInterface && !Modifier.isAbstract(it.modifiers) }.sortedBy { it.name })
        }

        // Find all scripted class of the given type
        results.addAll(ScriptManager.subTypes(type))

        cache[type] = results
        return results
    }

    fun setChoices(choiceParameter: GroupedChoiceParameter<Class<*>>, type: Class<*>) {
        val value = choiceParameter.value
        choiceParameter.clear()
        subTypes(type).groupBy { it.`package` }.forEach { pack, list ->
            val group = choiceParameter.group(pack?.name ?: "Top-Level")
            list.forEach { klass ->
                group.choice(klass.name, klass, klass.simpleName)
            }
        }
        choiceParameter.value = value
    }

    fun setNullableChoices(choiceParameter: GroupedChoiceParameter<Class<*>?>, type: Class<*>) {
        val value = choiceParameter.value
        choiceParameter.clear()
        choiceParameter.addChoice("", null, "<none>")

        subTypes(type).groupBy { it.`package` }.forEach { pack, list ->
            val group = choiceParameter.group(pack?.name ?: "Top-Level")
            list.forEach { klass ->
                group.choice(klass.name, klass, klass.simpleName)
            }
        }
        choiceParameter.value = value
    }
}



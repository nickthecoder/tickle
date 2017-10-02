package uk.co.nickthecoder.tickle.editor.util

import org.reflections.Reflections
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter


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
        reflectionsMap.values.forEach {
            results.addAll(it.getSubTypesOf(type).filter { !it.isInterface && !it.simpleName.startsWith("Abstract") }.sortedBy { it.name })
        }
        cache[type] = results

        return results
    }

    fun setChoices(choiceParameter: ChoiceParameter<Class<*>>, type: Class<*>) {
        val value = choiceParameter.value
        choiceParameter.clear()
        subTypes(type).forEach { klass ->
            choiceParameter.addChoice(klass.name, klass, klass.simpleName)
        }
        choiceParameter.value = value
    }

    fun setNullableChoices(choiceParameter: ChoiceParameter<Class<*>?>, type: Class<*>) {
        val value = choiceParameter.value
        choiceParameter.clear()
        choiceParameter.addChoice("", null, "<none>")
        subTypes(type).forEach { klass ->
            choiceParameter.addChoice(klass.name, klass, klass.simpleName)
        }
        choiceParameter.value = value
    }
}



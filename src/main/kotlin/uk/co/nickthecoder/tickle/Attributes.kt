package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.util.Attribute
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

/**
 * Holds a set of user-definable attributes. These are used to set property fields on Role objects, for values
 * defined in the Editor and SceneEditor.
 *
 * Annotate properties in Role with "@CostumeAttribute" or "@Attribute".
 * When using "@CostumeAttribute", the value is defined once in the Costume.
 * When using "@Attribute", the each instance of the Role can have a different value, and is defined in the
 * SceneEditor, for each Actor added to the scene.
 *
 * Example.
 *
 * A game has collectable items, so we define a Role called "Collectable".
 * We have several types of collectable (maybe coins of various colours and values).
 * So we create many Costumes, each with their own pose.
 * So we place the "@CostumeAttribute" on the Collectable class's "value" property.
 * Now we can use the Editor to assign a value for each of the costumes.
 *
 * If we want the collectable to move about, and every collectable can move a a different speed, then
 * we need a "speed" property on the Collectable class with the "@Attribute" annotation.
 * We can then set the speed for each collectable item from within the SceneEditor.
 *
 * Currently, only the following attribute types are supported :
 * Boolean, Int, Float, Double, String
 */
class Attributes {

    val map = mutableMapOf<String, String>()

    /**
     * Updates the object's property fields with the stored attribute values.
     */
    fun applyToObject(obj: Any) {
        val klass = obj.javaClass

        map.forEach { name, value ->
            updateAttribute(obj, klass, name, value)
        }
    }

    private fun updateAttribute(obj: Any, klass: Class<*>, name: String, value: String) {
        try {
            try {
                val field = klass.getField(name)
                field.set(obj, fromString(value, field.type))
            } catch (e: NoSuchFieldException) {
                val setterName = "set" + name.capitalize()
                val setter = klass.methods.filter { it.name == setterName && it.parameterCount == 1 }.firstOrNull()
                if (setter == null) {
                    System.err.println("ERROR. Could not find attribute $name in $obj")
                } else {
                    val type = setter.parameterTypes[0]
                    setter.invoke(obj, fromString(value, type))
                }
            }
        } catch (e: Exception) {
            System.err.println("Failed to set attribute $name on $obj : $e")
        }
    }


    companion object {

        fun createParameters(jClass: Class<*>, annotation: KClass<*> = Attribute::class): List<ValueParameter<*>> {
            val parameters = mutableListOf<ValueParameter<*>>()

            val kClass = jClass.kotlin
            kClass.members.filter {
                it.annotations.filter { it.annotationClass == annotation }.isNotEmpty()
            }.forEach { property ->
                createParameter(property.name, property.returnType.jvmErasure)?.let {
                    parameters.add(it)
                }
            }

            return parameters
        }

        fun createParameter(name: String, klass: Class<*>): ValueParameter<*>? {
            return createParameter(name, klass.kotlin)
        }

        fun createParameter(name: String, klass: KClass<*>): ValueParameter<*>? {

            return when (klass) {

                Boolean::class -> {
                    BooleanParameter("attribute_" + name, required = false, label = name)
                }
                Int::class -> {
                    IntParameter("attribute_" + name, required = false, label = name)
                }
                Float::class, Double::class -> {
                    DoubleParameter("attribute_" + name, required = false, label = name)
                }
                String::class -> {
                    StringParameter("attribute_" + name, required = false, label = name)
                }
                else -> {
                    System.err.println("Type $klass (for attribute $name) is not currently supported.")
                    null
                }
            }
        }

        fun fromString(value: String, klass: Class<*>): Any {
            return fromString(value, klass.kotlin)
        }

        fun fromString(value: String, klass: KClass<*>): Any {
            return when (klass) {
                Boolean::class -> value.toBoolean()
                Int::class -> value.toInt()
                Float::class -> value.toFloat()
                Double::class -> value.toDouble()
                String::class -> value
                else -> throw IllegalArgumentException("Type $klass is not currently supported.")
            }
        }

        fun attributeName(parameter: Parameter) = parameter.name.substring("attribute_".length)
    }

}

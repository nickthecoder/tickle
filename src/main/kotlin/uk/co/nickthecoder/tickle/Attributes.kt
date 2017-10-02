package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.util.Attribute
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

class Attributes {

    val map = mutableMapOf<String, String>()

    fun updateRole(role: Role) {
        val klass = role.javaClass

        map.forEach { name, value ->
            updateAttribute(role, klass, name, value)
        }
    }

    fun updateAttribute(role: Role, klass: Class<Role>, name: String, value: String) {
        try {
            try {
                val field = klass.getField(name)
                field.set(role, fromString(value, field.type))
            } catch (e: NoSuchFieldException) {
                val setterName = "set" + name.capitalize()
                val setter = klass.methods.filter { it.name == setterName && it.parameterCount == 1 }.firstOrNull()
                if (setter == null) {
                    System.err.println("ERROR. Could not find attribute $name in $role")
                } else {
                    val type = setter.parameterTypes[0]
                    setter.invoke(role, fromString(value, type))
                }
            }
        } catch (e: Exception) {
            System.err.println("Failed to set attribute $name on $role : $e")
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
                Boolean::class.java -> value.toBoolean()
                Int::class.java -> value.toInt()
                Float::class.java -> value.toFloat()
                Double::class.java -> value.toDouble()
                String::class.java -> value
                else -> throw IllegalArgumentException("Type $klass is not currently supported.")
            }
        }

        fun attributeName(parameter: Parameter) = parameter.name.substring("attribute_".length)
    }

}

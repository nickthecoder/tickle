package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.editor.util.AngleParameter
import uk.co.nickthecoder.tickle.editor.util.PolarParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute
import uk.co.nickthecoder.tickle.util.Polar2d
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

    private val map = mutableMapOf<String, AttributeData>()

    fun clear() {
        map.clear()
    }

    fun getValue(name: String) = map[name]?.value

    fun setValue(name: String, value: String) {
        getOrCreateData(name).value = value
    }

    fun map(): Map<String, AttributeData> = map

    fun data(): Collection<AttributeData> = map.values

    fun getOrCreateData(name: String): AttributeData {
        map[name]?.let { return it }
        val data = AttributeData()
        map[name] = data
        return data
    }

    /**
     * Updates the object's property fields with the stored attribute values.
     */
    fun applyToObject(obj: Any) {
        val klass = obj.javaClass

        map.toMap().forEach { name, data ->
            updateAttribute(obj, klass, name, data.value)
        }
    }

    private fun updateAttribute(obj: Any, klass: Class<*>, name: String, value: String?) {
        if (value == null) return
        try {
            try {
                val field = klass.getField(name)
                field.set(obj, fromString(value, field.type))
            } catch (e: NoSuchFieldException) {
                val setterName = "set" + name.capitalize()
                val setter = klass.methods.filter { it.name == setterName && it.parameterCount == 1 }.firstOrNull()
                if (setter == null) {
                    System.err.println("Warning. Could not find attribute '$name'. Make sure it's a var of class : $obj")
                    map.remove(name)
                } else {
                    val type = setter.parameterTypes[0]
                    setter.invoke(obj, fromString(value, type))
                }
            }
        } catch (e: Exception) {
            System.err.println("Failed to set attribute $name on $obj : $e")
        }
    }

    fun updateAttributeMetaData(className: String) {

        val kClass: KClass<*>
        try {
            kClass = Class.forName(className).kotlin
        } catch (e: Exception) {
            // Do nothing
            return
        }

        val toDiscard = map.keys.toMutableSet()

        kClass.members.forEach { property ->
            property.annotations.filterIsInstance<Attribute>().firstOrNull()?.let { annotation ->
                val data = getOrCreateData(property.name)
                data.attributeType = annotation.attributeType
                data.order = annotation.order
                data.scale = annotation.scale
                createParameter(property.name, property.returnType.jvmErasure)?.let { parameter ->
                    data.parameter = parameter
                    parameter.listen { data.value = parameter.stringValue }
                }
                toDiscard.remove(property.name)
            }
            property.annotations.filterIsInstance<CostumeAttribute>().firstOrNull()?.let { annotation ->
                val data = getOrCreateData(property.name)
                createParameter(property.name, property.returnType.jvmErasure)?.let { parameter ->
                    data.costumeParameter = parameter
                    parameter.listen { data.value = parameter.stringValue }
                }
                toDiscard.remove(property.name)
            }
        }

        toDiscard.forEach { name ->
            System.err.println("Warning. Removing attribute : $name. Not used by class '$className'.")
            map.remove(name)
        }

    }

    private fun createParameter(name: String, klass: KClass<*>): ValueParameter<*>? {

        return when (klass) {

            Boolean::class -> {
                BooleanParameter("attribute_$name", required = false, label = name)
            }
            Int::class -> {
                IntParameter("attribute_$name", required = false, label = name)
            }
            Float::class, Double::class -> {
                DoubleParameter("attribute_$name", required = false, label = name)
            }
            String::class -> {
                StringParameter("attribute_$name", required = false, label = name)
            }
            Polar2d::class -> {
                PolarParameter("attribute_$name", label = name)
            }
            Vector2d::class -> {
                Vector2dParameter("attribute_$name", label = name)
            }
            Angle::class -> {
                AngleParameter("attribute_$name", label = name)
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
            Polar2d::class -> Polar2d.fromString(value)
            Vector2d::class -> vector2dFromString(value)
            Angle::class -> Angle.degrees(value.toDouble())
            else -> throw IllegalArgumentException("Type $klass is not currently supported.")
        }
    }

}

data class AttributeData(
        var value: String? = null,
        var attributeType: AttributeType = AttributeType.NORMAL,
        var order: Int = 0,
        var scale: Double = 1.0,
        var parameter: ValueParameter<*>? = null,
        var costumeParameter: ValueParameter<*>? = null)


fun vector2dToString(vector: Vector2d) = "${vector.x}x${vector.y}"
fun vector2dFromString(string: String): Vector2d {
    val split = string.split("x")
    return Vector2d(split[0].toDouble(), split[1].toDouble())
}

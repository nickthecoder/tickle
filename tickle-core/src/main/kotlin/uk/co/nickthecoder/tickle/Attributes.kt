package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import org.joml.Vector2i
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.editor.util.AngleParameter
import uk.co.nickthecoder.tickle.editor.util.PolarParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute
import uk.co.nickthecoder.tickle.util.Polar2d
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

/**
 * Holds a set of user-definable attributes.
 *
 * Annotate properties in Role with "@CostumeAttribute" or "@Attribute".
 * When using "@CostumeAttribute", the value is defined once in the Costume.
 * When using "@Attribute", the each instance of the Role can have a different value, and is defined in the
 * SceneEditor, for each Actor added to the scene.
 *
 * Attributes are also used for StageConstraints, and will be used  with other classes as Tickle gains more features.
 *
 * Example.
 *
 * A game has collectable items of different values, so we define a Role called "Collectable",
 * with an field called "value" of type int.
 * We create a Costume for each type of collectable item, each with have its own pose.
 * We place the "@CostumeAttribute" on the Collectable class's "value" property.
 * Now we can use the Editor to assign a value for each of the costumes.
 *
 * If we want the collectable to move about, and every collectable can move at different speeds, then
 * we need a "speed" field on the Collectable class with the "@Attribute" annotation.
 * We can then set the speed for each collectable item from within the SceneEditor.
 *
 */
interface Attributes {

    fun clear()

    fun setValue(name: String, value: String)

    fun map(): Map<String, AttributeData>

    fun data(): Collection<AttributeData>

    fun getOrCreateData(name: String): AttributeData

    fun applyToObject(obj: Any)

    fun updateAttributesMetaData(className: String)
}

open class RuntimeAttributes : Attributes {

    protected val map = mutableMapOf<String, AttributeData>()

    override fun clear() {
        map.clear()
    }

    override fun setValue(name: String, value: String) {
        getOrCreateData(name).value = value
    }

    override fun map(): Map<String, AttributeData> = map

    override fun data(): Collection<AttributeData> = map.values

    override fun getOrCreateData(name: String): AttributeData {
        map[name]?.let { return it }
        val data = AttributeData()
        map[name] = data
        return data
    }

    /**
     * Updates the Role's fields (or other class's fields that uses Attributes) with the stored attribute values.
     *
     */
    override fun applyToObject(obj: Any) {
        val klass = obj.javaClass.kotlin

        map.toMap().forEach { name, data ->
            updateAttribute(obj, klass, name, data.value)
        }
    }

    /**
     * Updates a field on the Role (or other user created class that uses Attributes).
     */
    private fun updateAttribute(obj: Any, klass: KClass<*>, name: String, value: String?) {
        if (value == null) return
        try {
            val property = klass.memberProperties.filterIsInstance<KProperty1<Any?, Any?>>().firstOrNull { it.name == name }
            if (property == null) {
                System.err.println("ERROR. Could not find a mutable property (var) called '$name' on class '${klass.qualifiedName}'")
            } else {
                if (property is KMutableProperty1<Any?, Any?>) {
                    property.set(obj, fromString(value, property.returnType.jvmErasure))
                } else {
                    changeAttribute(property.get(obj), value, property.returnType.jvmErasure)
                }
            }

        } catch (e: Exception) {
            System.err.println("ERROR. Failed to set property '$name' on class '$klass'. Reason : $e")
        }
    }


    /**
     * Class fields tagged with @Attribute can either be simple mutable ('var') values, such as a Double, or 'val's
     * of classes whose fields are mutable such as
     *     val intialAngle : Angle = ...
     * This method is used by [updateAttribute] to alter the later kind (i.e. val fields that are not mutable, but
     * whose fields are mutable).
     */
    private fun changeAttribute(obj: Any?, value: String, klass: KClass<*>) {
        if (obj is Polar2d) {
            val v = Polar2d.fromString(value)
            obj.angle.radians = v.angle.radians
            obj.magnitude = v.magnitude
            return

        } else if (obj is Vector2d) {
            val v = vector2dFromString(value)
            obj.x = v.x
            obj.y = v.y

        } else if (obj is Angle) {
            val v = Angle.degrees(value.toDouble())
            obj.radians = v.radians

        } else if (klass == Boolean::class || klass == Int::class || klass == Float::class || klass == Double::class || klass == String::class) {

            throw IllegalArgumentException("Cannot change immutable type $klass.")

        } else {
            throw IllegalArgumentException("Type $klass is not currently supported.")
        }
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
            Color::class -> Color.fromString(value)
            else -> throw IllegalArgumentException("Type $klass is not currently supported.")
        }
    }


    override fun toString(): String {
        return "Attributes : " + map.map { (name, data) ->
            "$name=${data.value}"
        }.joinToString()
    }

    override fun updateAttributesMetaData(className: String) {
    }

}

class DesignAttributes : RuntimeAttributes() {

    /**
     * Uses reflection to scan the Class for fields using the @Attribute or @CostumeAttribute annotations.
     * Creates Parameters for each annotated field. The Parameter is given a ParameterListener which updates the
     * string representation of the data whenever the parameter changes.
     *
     * When isDesigning == true, an instance of the Role (or other class that uses Attributes) is created, so that
     * we can find the default value the field has immediately after creation. In this way, we can show the default
     * value in the Editor/SceneEditor.
     */
    override fun updateAttributesMetaData(className: String) {

        val kClass: KClass<*>
        var instance: Any? = null
        try {
            kClass = Class.forName(className).kotlin
            if (className.isNotBlank()) {
                instance = kClass.java.newInstance()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Do nothing
            return
        }

        /*
         * Copy the list of attribute names, and as we find their corresponding property, remove them from the list
         * Any names remaining are old attributes, but the class no longer has that property, so the attribute should
         * be removed.
         */
        val toDiscard = map.keys.toMutableSet()

        // TODO Should this be memberProperties rather than members?
        kClass.members.forEach { property ->
            property.annotations.filterIsInstance<Attribute>().firstOrNull()?.let { annotation ->
                val hasExistingValue = map[property.name]?.value != null
                val data = getOrCreateData(property.name)
                data.attributeType = annotation.attributeType
                data.order = annotation.order
                data.scale = annotation.scale

                createParameter(property.name, property.returnType.jvmErasure, hasAlpha = annotation.hasAlpha)?.let { parameter ->
                    data.parameter = parameter
                    parameter.listen { data.value = parameter.stringValue }

                    if (!hasExistingValue && instance != null) {
                        // I believe this is safe, because this class creates the parameters based on the return type
                        // of the property. So this is safe as long as createParameter is correct.
                        @Suppress("UNCHECKED_CAST")
                        val theValue = (property as KProperty1<Any, Any>).get(instance!!)
                        @Suppress("UNCHECKED_CAST")
                        (data.parameter as ValueParameter<Any>).value = theValue
                        data.value = data.parameter!!.stringValue
                    } else {
                        data.value?.let { data.parameter!!.stringValue = it }
                    }
                }
                toDiscard.remove(property.name)
            }
            property.annotations.filterIsInstance<CostumeAttribute>().firstOrNull()?.let { annotation ->
                val data = getOrCreateData(property.name)
                createParameter(property.name, property.returnType.jvmErasure, hasAlpha = annotation.hasAlpha)?.let { parameter ->
                    data.costumeParameter = parameter
                    data.order = annotation.order
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

    private fun createParameter(name: String, klass: KClass<*>, hasAlpha: Boolean): ValueParameter<*>? {

        return when (klass) {

            Boolean::class -> {
                BooleanParameter("attribute_$name", required = false, label = name)
            }
            Int::class -> {
                IntParameter("attribute_$name", required = false, label = name)
            }
            Float::class -> {
                FloatParameter("attribute_$name", required = false, minValue = -Float.MAX_VALUE, label = name)
            }
            Double::class -> {
                DoubleParameter("attribute_$name", required = false, minValue = -Double.MAX_VALUE, label = name)
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
            Color::class -> {
                if (hasAlpha) {
                    AlphaColorParameter("attribute_$name", label = name)
                } else {
                    ColorParameter("attribute_$name", label = name)
                }
            }
            else -> {
                System.err.println("Type $klass (for attribute $name) is not currently supported.")
                null
            }
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

fun vector2iToString(vector: Vector2i) = "${vector.x}x${vector.y}"
fun vector2iFromString(string: String): Vector2i {
    val split = string.split("x")
    return Vector2i(split[0].toInt(), split[1].toInt())
}
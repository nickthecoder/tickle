package uk.co.nickthecoder.tickle.editor.util

import org.joml.Vector2d
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.AttributeData
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.RuntimeAttributes
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute
import uk.co.nickthecoder.tickle.util.Polar2d
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure


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
    override fun updateAttributesMetaData(klass: Class<*>) {

        val kClass = klass.kotlin
        var instance: Any? = null
        try {
            instance = klass.newInstance()
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
            System.err.println("Warning. Removing attribute : $name. Not used by class '$klass'.")
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

    override fun getOrCreateData(name: String): DesignAttributeData {
        map[name]?.let { return it as DesignAttributeData }
        val data = DesignAttributeData()
        map[name] = data
        return data
    }

}

class DesignAttributeData(
        value: String? = null,
        attributeType: AttributeType = AttributeType.NORMAL,
        order: Int = 0,
        scale: Double = 1.0,
        var parameter: ValueParameter<*>? = null,
        var costumeParameter: ValueParameter<*>? = null)

    : AttributeData(value, attributeType, order, scale)

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
package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Polar2d
import uk.co.nickthecoder.tickle.util.SimpleInstance
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

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
                System.err.println("WARNING. Could not find a property called '$name' on class '${klass.qualifiedName}'")
                map.remove(name)
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
            throw IllegalArgumentException("Type $klass is not currently supported (2)")
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

            else -> {
                if (klass.java.isEnum) {
                    @Suppress("UNCHECKED_CAST")
                    (klass as KClass<out Enum<*>>).java.enumConstants.filter { it.name == value }.first()
                } else if (SimpleInstance::class.java.isAssignableFrom(klass.java)) {
                    ScriptManager.classForName(value).newInstance()
                } else {
                    throw IllegalArgumentException("Type $klass is not currently supported (1).")
                }
            }
        }
    }


    override fun toString(): String {
        return "Attributes : " + map.map { (name, data) ->
            "$name=${data.value}"
        }.joinToString()
    }

    override fun updateAttributesMetaData(klass: Class<*>) {
    }

}

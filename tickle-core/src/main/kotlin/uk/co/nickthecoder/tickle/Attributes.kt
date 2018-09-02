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
import org.joml.Vector2i
import uk.co.nickthecoder.tickle.scripts.ScriptManager

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
 * There are two implementations.
 * [RuntimeAttributes], which is used during normal game-play.
 * DesignAttributes, which is used from within the Editor. It is split like this, because DesignAttributes
 * has dependencies on ParaTask (the GUI framework that the editor uses for editing sets of parameters).
 */
interface Attributes {

    fun clear()

    fun setValue(name: String, value: String)

    fun map(): Map<String, AttributeData>

    fun data(): Collection<AttributeData>

    fun getOrCreateData(name: String): AttributeData

    fun applyToObject(obj: Any)

    fun updateAttributesMetaData(name: String) {
        updateAttributesMetaData(ScriptManager.classForName(name))
    }

    fun updateAttributesMetaData(klass: Class<*>)
}


open class AttributeData(
        var value: String? = null,
        var attributeType: AttributeType = AttributeType.NORMAL,
        var order: Int = 0,
        var scale: Double = 1.0
)

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

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

import javafx.util.StringConverter
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.parameters.CompoundParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.vector2dFromString
import uk.co.nickthecoder.tickle.vector2dToString

class Vector2dParameter(
        name: String,
        label: String = name.uncamel(),
        value: Vector2d = Vector2d(),
        description: String = "",
        showXY: Boolean = true)

    : CompoundParameter<Vector2d>(
        name, label, description) {

    val xP = DoubleParameter("${name}_x", label = if (showXY) "X" else "", minValue = -Double.MAX_VALUE)
    var x by xP

    val yP = DoubleParameter("${name}_y", label = if (showXY) "Y" else ",", minValue = -Double.MAX_VALUE)
    var y by yP

    override val converter = object : StringConverter<Vector2d>() {
        override fun fromString(string: String): Vector2d {
            return vector2dFromString(string)
        }

        override fun toString(obj: Vector2d): String {
            return vector2dToString(obj)
        }
    }

    override var value: Vector2d
        get() {
            return Vector2d(xP.value ?: 0.0, yP.value ?: 0.0)
        }
        set(value) {
            xP.value = value.x
            yP.value = value.y
        }


    init {
        this.value = value

        addParameters(xP, yP)
    }

    override fun toString(): String {
        return "Vector2dParameter : $value"
    }

    override fun copy(): Vector2dParameter {
        val copy = Vector2dParameter(name, label, value, description)
        return copy
    }
}

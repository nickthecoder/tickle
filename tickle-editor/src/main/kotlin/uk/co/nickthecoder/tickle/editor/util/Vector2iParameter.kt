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
import org.joml.Vector2i
import uk.co.nickthecoder.paratask.parameters.CompoundParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.vector2iFromString
import uk.co.nickthecoder.tickle.vector2iToString


class Vector2iParameter(
        name: String,
        label: String = name.uncamel(),
        value: Vector2i = Vector2i(),
        description: String = "",
        showXY: Boolean = true)

    : CompoundParameter<Vector2i>(
        name, label, description) {

    val xP = IntParameter("${name}_x", label = if (showXY) "X" else "", minValue = -Int.MAX_VALUE)
    var x by xP

    val yP = IntParameter("${name}_y", label = if (showXY) "Y" else ",", minValue = -Int.MAX_VALUE)
    var y by yP

    override val converter = object : StringConverter<Vector2i>() {
        override fun fromString(string: String): Vector2i {
            return vector2iFromString(string)
        }

        override fun toString(obj: Vector2i): String {
            return vector2iToString(obj)
        }
    }

    override var value: Vector2i
        get() {
            return Vector2i(xP.value ?: 0, yP.value ?: 0)
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
        return "Vector2iParameter : $value"
    }

    override fun copy(): Vector2iParameter {
        val copy = Vector2iParameter(name, label, value, description)
        return copy
    }
}

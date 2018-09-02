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
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.util.Angle


class AngleParameter(
        name: String,
        label: String = name.uncamel(),
        value: Angle = Angle(),
        description: String = "")

    : CompoundParameter<Angle>(
        name, label, description) {

    val degreesP = DoubleParameter("${name}_degrees", minValue = -360.0, maxValue = 360.0)
    var degrees by degreesP

    override val converter = object : StringConverter<Angle>() {
        override fun fromString(string: String): Angle {
            return Angle.degrees(string.toDouble())
        }

        override fun toString(obj: Angle): String {
            return obj.degrees.toString()
        }
    }

    override var value: Angle
        get() {
            return Angle.degrees(degrees ?: 0.0)
        }
        set(value) {
            degrees = value.degrees
        }


    init {
        this.value = value

        addParameters(degreesP)
        asHorizontal(LabelPosition.NONE)
    }

    override fun toString(): String {
        return "AngleParameter : $value"
    }

    override fun copy(): AngleParameter {
        val copy = AngleParameter(name, label, value, description)
        return copy
    }
}

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
import uk.co.nickthecoder.paratask.parameters.CompoundParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Polar2d

class PolarParameter(
        name: String,
        label: String = name.uncamel(),
        value: Polar2d = Polar2d(),
        description: String = "")

    : CompoundParameter<Polar2d>(
        name, label, description) {

    val angleP = DoubleParameter("${name}_angle", label = "∠")
    var angle by angleP

    val magnitudeP = DoubleParameter("${name}_magnitude", label = "ρ")
    var magnitude by magnitudeP

    override val converter = object : StringConverter<Polar2d>() {
        override fun fromString(string: String): Polar2d {
            return Polar2d.fromString(string)
        }

        override fun toString(obj: Polar2d): String {
            return obj.toString()
        }
    }


    override var value: Polar2d
        get() {
            return Polar2d(Angle.degrees(angleP.value ?: 0.0), magnitudeP.value ?: 0.0)
        }
        set(value) {
            angleP.value = value.angle.degrees
            magnitudeP.value = value.magnitude
        }


    init {
        this.value = value

        addParameters(angleP, magnitudeP)
    }

    override fun toString(): String {
        return "PolarParameter : $value"
    }

    override fun copy(): PolarParameter {
        val copy = PolarParameter(name, label, value, description)
        return copy
    }
}

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

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel

/**
 * Despite Rect storing values with (left,bottom, right,top), I think it more intuitive to give a coordinate,
 * and a width and height.
 *
 * Note, this class can deal with the y axis pointing up or down. When dealing with images, it is normal for the
 * y axis to point down (i.e. 0,0 is the top left. In this case, "TOP" and "HEIGHT" are shown to the user.
 *
 * When the y axis points up, then "BOTTOM" and "HEIGHT" are shown to the user.
 */
class RectiParameter(
        name: String,
        override val label: String = name.uncamel(),
        val required: Boolean = true,
        val bottomUp: Boolean = true,
        description: String = "")

    : GroupParameter(
        name = name,
        label = label,
        description = description) {

    val leftP = IntParameter(name + "_left", label = "Left", required = required)
    var left by leftP

    val bottomP = IntParameter(name + "_bottom", label = "Bottom", required = required)

    val topP = IntParameter(name + "_top", label = "Top", required = required)

    val widthP = IntParameter(name + "_width", label = "Width", required = required)
    var width by widthP

    val heightP = IntParameter(name + "_height", label = "Height", required = required)
    var height by heightP

    var right: Int?
        get() = plus(left, width)
        set(v) {
            width = minus(v, left)
        }

    var top: Int?
        get() = if (bottomUp) plus(bottomP.value, heightP.value) else topP.value
        set(v) {
            if (bottomUp) {
                height = minus(v, bottomP.value)
            } else {
                topP.value = v
            }
        }

    var bottom: Int?
        get() = if (bottomUp) bottomP.value else plus(topP.value, heightP.value)
        set(v) {
            if (bottomUp) {
                bottomP.value = v
            } else {
                height = minus(v, topP.value)
            }
        }

    init {
        if (bottomUp) {
            addParameters(leftP, bottomP, widthP, heightP)
        } else {
            addParameters(leftP, topP, widthP, heightP)
        }
        asHorizontal(LabelPosition.TOP)
    }

    override fun saveChildren(): Boolean = true

    override fun errorMessage(): String? {
        return null
    }

    override fun copy(): RectiParameter {
        val copy = RectiParameter(name = name,
                label = label,
                description = description,
                bottomUp = bottomUp,
                required = required)
        copyAbstractAttributes(copy)
        return copy
    }

}

private fun plus(a: Int?, b: Int?): Int? {
    return if (a == null || b == null) null else a + b
}

private fun minus(a: Int?, b: Int?): Int? {
    return if (a == null || b == null) null else a - b
}

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


class XYiParameter(
        name: String,
        override val label: String = name.uncamel(),
        val required: Boolean = true,
        description: String = "")

    : GroupParameter(
        name = name,
        label = label,
        description = description) {

    val xP = IntParameter(name + "_x", label = "X", required = required)
    var x by xP

    val commaP = InformationParameter(name + "_comma", information = ",")

    val yP = IntParameter(name + "_y", label = "Y", required = required)
    var y by yP

    init {
        addParameters(xP, commaP, yP)

        asHorizontal(LabelPosition.NONE)
    }

    override fun isStretchy() = false

    override fun saveChildren(): Boolean = true

    override fun errorMessage(): String? {
        return null
    }

    override fun copy(): XYiParameter {
        val copy = XYiParameter(name = name,
                label = label,
                description = description,
                required = required)
        copyAbstractAttributes(copy)
        return copy
    }

}

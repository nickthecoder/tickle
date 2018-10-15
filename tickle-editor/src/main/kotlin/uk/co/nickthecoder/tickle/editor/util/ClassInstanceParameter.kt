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

import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.scripts.ScriptManager

class ClassInstanceParameter(
        name: String,
        type: Class<*>,
        label: String = name.uncamel(),
        val required: Boolean = false,
        value: Any? = null)
    : ClassParameter(name, type, label, required, value?.javaClass), ValueParameter<Any?> {

    override var value: Any?
        get() = classValue?.newInstance()
        set(v) {
            classValue = v?.javaClass
        }

    override val converter = object : StringConverter<Any?>() {
        override fun fromString(string: String?): Any? {
            if (string == null) return null
            return ScriptManager.classForName(string).newInstance()
        }

        override fun toString(obj: Any?) = obj?.javaClass?.name
    }

    override fun errorMessage(v: Any?): String? = null

    override fun errorMessage(): String? = errorMessage(value)

    override val expressionProperty = SimpleStringProperty()

    override fun copy() = ClassInstanceParameter(
            name,
            type,
            label,
            required,
            value
    )

}

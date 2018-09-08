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

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

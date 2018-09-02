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

import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.parameters.AbstractParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.ParameterEventType
import uk.co.nickthecoder.paratask.parameters.ParameterListener
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.uncamel

class ImageParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        val image: Image,
        val fieldFactory: (ImageParameter) -> ParameterField = { ImageParameterField(it) })

    : AbstractParameter(name, label, description) {

    var viewPort: Rectangle2D? = null
        set(v) {
            field = v
            parameterListeners.fireStructureChanged(this)
        }

    override fun isStretchy() = false

    override fun errorMessage() = null

    override fun createField() = fieldFactory(this).build()

    override fun copy() = ImageParameter(name, label, description, image)
}

open class ImageParameterField(val imageParameter: ImageParameter)

    : ParameterField(imageParameter), ParameterListener {

    val imageView = ImageView(imageParameter.image)

    open override fun createControl(): Node {
        imageView.viewportProperty().set(imageParameter.viewPort)
        return imageView
    }

    open override fun parameterChanged(event: ParameterEvent) {
        if (event.type == ParameterEventType.STRUCTURAL) {
            imageView.viewportProperty().set(imageParameter.viewPort)
        }
    }
}

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

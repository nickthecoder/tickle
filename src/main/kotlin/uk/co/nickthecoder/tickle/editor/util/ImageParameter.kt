package uk.co.nickthecoder.tickle.editor.util

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.parameters.AbstractParameter
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.uncamel

class ImageParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        val image: Image)

    : AbstractParameter(name, label, description) {


    override fun isStretchy() = false

    override fun errorMessage() = null

    override fun createField() = ImageParameterField(this).build()

    override fun copy() = ImageParameter(name, label, description, image)
}

class ImageParameterField(val imageParameter: ImageParameter)

    : ParameterField(imageParameter) {

    override fun createControl(): ImageView {
        println("Creating image view from ${imageParameter.image}")
        return ImageView(imageParameter.image)
    }
}

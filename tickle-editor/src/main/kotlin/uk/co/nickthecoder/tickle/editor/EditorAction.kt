package uk.co.nickthecoder.tickle.editor

import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.gui.ApplicationAction

class EditorAction(
        name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        tooltip: String? = null,
        label: String? = null) : ApplicationAction(name, keyCode, shift, control, alt, meta, shortcut, tooltip, label) {

    override val image: Image? = imageResource("$name.png")


    companion object {

        private val imageMap = mutableMapOf<String, Image?>()

        fun imageResource(name: String): Image? {
            val image = imageMap[name]
            if (image == null) {
                val imageStream = EditorAction::class.java.getResourceAsStream(name)
                val newImage = if (imageStream == null) null else Image(imageStream)
                imageMap.put(name, newImage)
                return newImage
            }
            return image
        }
    }
}

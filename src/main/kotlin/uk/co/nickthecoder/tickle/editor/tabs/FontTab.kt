package uk.co.nickthecoder.tickle.editor.tabs

import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ButtonParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.editor.util.FontParameter
import uk.co.nickthecoder.tickle.editor.util.ImageCache
import uk.co.nickthecoder.tickle.editor.util.NewResourceTask
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File
import javax.imageio.ImageIO


class FontTab(name: String, val fontResource: FontResource)

    : EditTaskTab(FontTask(name, fontResource), name, data = fontResource, graphicName = "font.png") {

    init {
        addDeleteButton { Resources.instance.fontResources.remove(name) }

        val createCostumeButton = Button("Create Costume")
        createCostumeButton.setOnAction { (task as FontTask).createCostume() }
        leftButtons.children.add(createCostumeButton)
    }
}

class FontTask(val name: String, val fontResource: FontResource) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val fontP = FontParameter("font")

    val infoP = InformationParameter("info", information = """Generate a .png file if you wish to edit the font, so that you can edit it in photo editing program such as Gimp.
In addition, you can create another .png file if you wish to add an outline to the font. It should be named <FONTNAME>-outline.png.""")

    val xPaddingP = IntParameter("xPadding", value = fontResource.xPadding)
    val yPaddingP = IntParameter("yPadding", value = fontResource.yPadding)

    val generateButtonP = ButtonParameter("generatePNG", buttonText = "Generate PNG File") { generatePngAndMetrics() }

    override val taskD = TaskDescription("editFont")
            .addParameters(nameP, fontP, infoP, xPaddingP, yPaddingP, generateButtonP)

    init {
        fontP.from(fontResource)
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.fontResources.rename(name, nameP.value)
        }
        fontResource.xPadding = xPaddingP.value!!
        fontResource.yPadding = yPaddingP.value!!

        fontP.update(fontResource)
    }


    fun generatePngAndMetrics() {

        try {
            check()
            run()
        } catch(e: Exception) {
            return
        }

        val image = ImageCache.image(fontResource.fontTexture.glyphs.values.first().pose.texture)
        val bImage = SwingFXUtils.fromFXImage(image, null)
        val name = nameP.value
        val pngFile = File(Resources.instance.texturesDirectory, "$name.png")
        ImageIO.write(bImage, "png", pngFile)

        val metricsFile = File(Resources.instance.texturesDirectory, "$name.metrics")
        JsonResources.saveFontMetrics(metricsFile, fontResource)

    }

    fun createCostume() {
        val task = NewResourceTask(fontResource, nameP.value)
        task.prompt()
    }


}

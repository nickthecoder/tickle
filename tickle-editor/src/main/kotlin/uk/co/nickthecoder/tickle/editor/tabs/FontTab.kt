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
package uk.co.nickthecoder.tickle.editor.tabs

import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ButtonParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.editor.resources.DesignJsonResources
import uk.co.nickthecoder.tickle.editor.util.FontParameter
import uk.co.nickthecoder.tickle.editor.util.ImageCache
import uk.co.nickthecoder.tickle.editor.util.NewResourceTask
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Resources
import java.io.File
import javax.imageio.ImageIO


class FontTab(name: String, fontResource: FontResource)

    : EditTaskTab(FontTask(name, fontResource), name, data = fontResource, graphicName = "font.png") {

    init {
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

        fontResource.clearCache()
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
        DesignJsonResources.saveFontMetrics(metricsFile, fontResource)

    }

    fun createCostume() {
        val task = NewResourceTask(fontResource, nameP.value)
        task.prompt()
    }


}

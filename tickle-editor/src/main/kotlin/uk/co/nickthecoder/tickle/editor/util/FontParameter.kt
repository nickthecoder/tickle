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

import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.resources.FontResource
import java.awt.Font


class FontParameter(name: String)

    : SimpleGroupParameter(name) {

    val fontNameP = StringParameter("fontName", label = "", value = Font.SANS_SERIF, columns = 20)
    val fontStyleP = ChoiceParameter<FontResource.FontStyle>("fontStyle", label = "", value = FontResource.FontStyle.PLAIN).enumChoices(mixCase = true)
    val fontNameAndStyleP = SimpleGroupParameter("fontNameAndStyle", label = "")
            .addParameters(fontNameP, fontStyleP).asHorizontal()

    val fontFileP = FileParameter("fontFile", label = "", extensions = listOf("ttf", "otf"))
    val fontOneOfP = OneOfParameter("fontChoice", label = "Font", value = fontNameAndStyleP, choiceLabel = "")
            .addChoices("Named" to fontNameAndStyleP, "From File" to fontFileP)

    val fontSizeP = DoubleParameter("fontSize", label = "Size", value = 22.0)


    init {
        addParameters(fontOneOfP, fontNameAndStyleP, fontFileP, fontSizeP)
    }

    fun update(fontResource: FontResource) {

        if (fontOneOfP.value == fontNameAndStyleP) {
            fontResource.fontName = fontNameP.value
            fontResource.style = fontStyleP.value!!
            fontResource.file = null
        } else {
            fontResource.fontName = ""
            fontResource.style = FontResource.FontStyle.PLAIN
            fontResource.file = fontFileP.value
        }

        fontResource.size = fontSizeP.value!!

        try {
            fontResource.fontTexture
        } catch(e: Exception) {
            throw ParameterException(fontOneOfP, "Could not load/create the font.")
        }
    }

    fun from(fontResource: FontResource) {
        if (fontResource.file == null) {
            fontOneOfP.value = fontNameAndStyleP
            fontNameP.value = fontResource.fontName
            fontStyleP.value = fontResource.style
            fontFileP.value = null
        } else {
            fontOneOfP.value = fontFileP
            fontNameP.value = ""
            fontStyleP.value = FontResource.FontStyle.PLAIN
            fontFileP.value = fontResource.file
        }
        fontSizeP.value = fontResource.size
    }
}

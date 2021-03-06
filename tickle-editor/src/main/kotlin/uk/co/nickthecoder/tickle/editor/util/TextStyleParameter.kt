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

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextHAlignment
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.graphics.TextVAlignment

class TextStyleParameter(name: String)

    : SimpleGroupParameter(name) {

    val fontP = createFontParameter("font")
    val colorP = AlphaColorParameter("color", label = "Colour")
    val outlineColorP = AlphaColorParameter("outlineColor", label = "Outline Colour")
    val hAlignmentP = ChoiceParameter<TextHAlignment>("xAlignment", value = TextHAlignment.LEFT).enumChoices(mixCase = true)
    val vAlignmentP = ChoiceParameter<TextVAlignment>("yAlignment", value = TextVAlignment.TOP).enumChoices(mixCase = true)

    init {
        addParameters(fontP, colorP, outlineColorP, hAlignmentP, vAlignmentP)
        outlineColorP.hidden = fontP.value?.outlineFontTexture == null
        fontP.listen { outlineColorP.hidden = fontP.value?.outlineFontTexture == null }
    }

    fun from(textStyle: TextStyle) {
        fontP.value = textStyle.fontResource
        colorP.value = textStyle.color.toJavaFX()
        outlineColorP.value = (textStyle.outlineColor ?: Color.black().transparent()).toJavaFX()
        hAlignmentP.value = textStyle.halignment
        vAlignmentP.value = textStyle.valignment
    }

    fun update(textStyle: TextStyle) {
        textStyle.fontResource = fontP.value!!
        textStyle.color = colorP.value.toTickle()
        textStyle.outlineColor = outlineColorP.value.toTickle()
        textStyle.halignment = hAlignmentP.value!!
        textStyle.valignment = vAlignmentP.value!!
    }

    fun createTextStyle(): TextStyle {
        return TextStyle(
                fontP.value!!,
                color = colorP.value.toTickle(),
                outlineColor = outlineColorP.value.toTickle(),
                halignment = hAlignmentP.value!!,
                valignment = vAlignmentP.value!!)
    }
}

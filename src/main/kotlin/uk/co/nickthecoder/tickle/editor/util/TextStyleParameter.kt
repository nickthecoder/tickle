package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.editor.scene.toJavaFX
import uk.co.nickthecoder.tickle.editor.scene.toTickle
import uk.co.nickthecoder.tickle.graphics.HAlignment
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.graphics.VAlignment

class TextStyleParameter(name: String)

    : SimpleGroupParameter(name) {

    val fontP = createFontParameter("font")
    val colorP = ColorParameter("color", label = "Colour")
    val hAlignmentP = ChoiceParameter<HAlignment>("xAligment", value = HAlignment.LEFT).enumChoices(mixCase = true)
    val vAlignmentP = ChoiceParameter<VAlignment>("yAligment", value = VAlignment.TOP).enumChoices(mixCase = true)

    init {
        addParameters(fontP, colorP, hAlignmentP, vAlignmentP)
    }

    fun from(textStyle: TextStyle) {
        fontP.value = textStyle.fontResource
        colorP.value = textStyle.color.toJavaFX()
        hAlignmentP.value = textStyle.halignment
        vAlignmentP.value = textStyle.valignment
    }

    fun update(textStyle: TextStyle) {
        textStyle.fontResource = fontP.value!!
        textStyle.color = colorP.value.toTickle()
        textStyle.halignment = hAlignmentP.value!!
        textStyle.valignment = vAlignmentP.value!!
    }

    fun createTextStyle(): TextStyle {
        return TextStyle(fontP.value!!, color = colorP.value.toTickle(), halignment = hAlignmentP.value!!, valignment = vAlignmentP.value!!)
    }
}

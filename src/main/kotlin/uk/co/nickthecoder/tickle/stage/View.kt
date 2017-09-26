package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.graphics.Renderer

interface View {

    var rect: Rectangle

    fun draw(renderer: Renderer)

}

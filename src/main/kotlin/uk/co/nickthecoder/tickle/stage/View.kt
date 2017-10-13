package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.util.Recti

interface View {

    var zOrder: Int

    var rect: Recti

    fun draw(renderer: Renderer)


}

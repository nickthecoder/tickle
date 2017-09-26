package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.math.toRadians

class ZOrderStageView(

        override var rect: Rectangle,
        override val stage: Stage)
    : StageView {

    var centerX = 0f;
    var centerY = 0f;
    var degrees = 0.0

    override fun draw(renderer: Renderer) {
        // TODO Use the view's size and position. Currently it uses the whole window.
        renderer.rotateView(centerX, centerY, toRadians(degrees))

        stage.actors.sortedBy { it.z }.forEach { actor ->
            actor.appearance.draw(renderer)
        }
    }

}

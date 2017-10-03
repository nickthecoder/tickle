package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.util.Recti

class ZOrderStageView()
    : StageView {

    override var rect = Recti(0, 0, 10, 10) //

    override lateinit var stage: Stage

    var centerX = 0f;
    var centerY = 0f;
    var degrees = 0.0

    override fun draw(renderer: Renderer) {
        // TODO Use the view's size and position. Currently it uses the whole window.
        renderer.rotateView(rect, centerX, centerY, Math.toRadians(degrees).toFloat())

        stage.actors.sortedBy { it.z }.forEach { actor ->
            actor.appearance.draw(renderer)
        }
    }

}

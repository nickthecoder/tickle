package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.graphics.Renderer

class ZOrderStageView
    : AbstractStageView() {

    override fun draw(renderer: Renderer) {
        super.draw(renderer)

        stage.actors.sortedBy { it.z }.forEach { actor ->
            actor.appearance.draw(renderer)
        }
    }

}

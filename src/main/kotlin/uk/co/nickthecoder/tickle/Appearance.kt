package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Renderer

interface Appearance {

    fun draw(renderer: Renderer)

}

class InvisibleAppearance : Appearance {
    override fun draw(renderer: Renderer) {
        // Do nothing
    }
}

class PoseAppearance(val actor: Actor, var pose: Pose) : Appearance {

    override fun draw(renderer: Renderer) {
        pose.draw(renderer, actor)
    }
}

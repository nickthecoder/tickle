package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Renderer

interface Appearance {

    val directionRadians: Double

    /**
     * The natural direction. i.e. if the Actor moved "forward", which mathematical angle would that be?
     * For the default value of 0, the image is pointing to the right.
     */
    fun draw(renderer: Renderer)

}

class InvisibleAppearance : Appearance {

    override val directionRadians = 0.0

    override fun draw(renderer: Renderer) {
        // Do nothing
    }

    override fun toString() = "InvisibleAppearance"
}

class PoseAppearance(val actor: Actor, var pose: Pose) : Appearance {

    override val directionRadians
        get() = pose.direction.radians

    override fun draw(renderer: Renderer) {
        pose.draw(renderer, actor)
    }

    override fun toString() = "PoseAppearance pose=$pose"
}

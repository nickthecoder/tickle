package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.TextStyle

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

class TextAppearance(val actor: Actor, var text: String, val textStyle: TextStyle) : Appearance {

    override val directionRadians = 0.0

    override fun draw(renderer: Renderer) {
        textStyle.draw(renderer, text, actor.x, actor.y)
    }

    override fun toString() = "TextAppearance '$text'"
}

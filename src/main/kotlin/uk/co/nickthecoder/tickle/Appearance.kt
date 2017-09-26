package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.math.toDegrees
import uk.co.nickthecoder.tickle.math.toRadians

interface Appearance {

    fun draw(renderer: Renderer)

    var directionRadians: Double

    var directionDegrees: Double

    var color: Color
}

abstract class AbstractAppearance : Appearance {

    override var directionRadians: Double = 0.0

    override var directionDegrees: Double
        get() = toDegrees(directionRadians)
        set(v) {
            directionRadians = toRadians(v)
        }

    override var color: Color = Color.WHITE
}

class InvisibleAppearance : AbstractAppearance() {
    override fun draw(renderer: Renderer) {
        // Do nothing
    }
}

class PoseAppearance(val actor: Actor, val pose: Pose) : AbstractAppearance() {

    override fun draw(renderer: Renderer) {
        pose.draw(renderer, actor)
    }
}

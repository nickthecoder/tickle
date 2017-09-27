package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.math.Vector2
import uk.co.nickthecoder.tickle.math.toDegrees
import uk.co.nickthecoder.tickle.math.toRadians
import uk.co.nickthecoder.tickle.stage.Stage

class Actor(val role: Role? = null) {

    init {
        role?.let {
            it.actor = this
        }
    }

    internal var stage: Stage? = null

    var x: Float = 0f

    var y: Float = 0f

    var z: Int = 0

    var directionRadians: Double = 0.0

    var directionDegrees: Double
        get() = toDegrees(directionRadians)
        set(v) {
            directionRadians = toRadians(v)
        }

    var color: Color = Color.WHITE

    var appearance: Appearance = InvisibleAppearance()

    fun setPosition(position: Vector2) {
        x = position.x
        y = position.y
    }

    fun changePose(pose: Pose) {
        appearance = PoseAppearance(this, pose)
    }

    // TODO Should there be a "changeText" method, similar to changePose?
    // Check if

    override fun toString() = "Actor @ $x,$y Role=$role"
}

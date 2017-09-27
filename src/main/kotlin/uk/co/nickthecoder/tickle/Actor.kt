package uk.co.nickthecoder.tickle

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.stage.Stage

class Actor(val role: Role? = null) {

    internal var stage: Stage? = null

    val position = Vector2f(0f, 0f)

    var x: Float
        get() = position.x
        set(v) {
            position.x = v
        }

    var y: Float
        get() = position.y
        set(v) {
            position.y = v
        }

    var z: Float = 0f

    var directionRadians: Double = 0.0

    var directionDegrees: Double
        get() = Math.toDegrees(directionRadians)
        set(v) {
            directionRadians = Math.toRadians(v)
        }

    var color: Color = Color.WHITE

    var appearance: Appearance = InvisibleAppearance()

    init {
        role?.actor = this
    }

    fun changePose(pose: Pose) {
        appearance = PoseAppearance(this, pose)
    }

    // TODO Should there be a "changeText" method, similar to changePose?

    fun die() {
        stage?.remove(this)
        role?.end()
    }

    override fun toString() = "Actor @ $x,$y Role=$role"
}

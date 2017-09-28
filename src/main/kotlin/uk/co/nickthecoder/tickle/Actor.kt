package uk.co.nickthecoder.tickle

import org.joml.Matrix4f
import org.joml.Vector2f
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.stage.Stage

private var nextId: Int = 0

class Actor(val role: Role? = null) {

    val id = nextId ++

    internal var stage: Stage? = null

    val position = Vector2f(0f, 0f)

    /**
     * Gets or sets the x value of position
     */
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
        set(v) {
            field = v
            dirtyMatrix = true
        }

    var directionDegrees: Double
        get() = Math.toDegrees(directionRadians)
        set(v) {
            directionRadians = Math.toRadians(v)
        }


    var scale: Float = 1f
        set(v) {
            field = v
            dirtyMatrix = true
        }

    var color: Color = Color.WHITE


    var appearance: Appearance = InvisibleAppearance()

    private val modelMatrix = Matrix4f()

    private var dirtyMatrix: Boolean = false

    init {
        role?.actor = this
    }

    fun getModelMatrix(): Matrix4f {
        if (dirtyMatrix) {
            modelMatrix.identity().translate(x, y, 0f).rotateZ(directionRadians.toFloat()).scale(scale).translate(-x, -y, 0f)
        }
        return modelMatrix
    }

    fun changePose(pose: Pose) {
        appearance = PoseAppearance(this, pose)
    }

    // TODO Should there be a "changeText" method, similar to changePose?

    fun die() {
        stage?.remove(this)
        role?.end()
    }

    override fun toString() = "Actor #$id @ $x,$y Role=${role?.javaClass?.simpleName ?: "<none>"}"
}

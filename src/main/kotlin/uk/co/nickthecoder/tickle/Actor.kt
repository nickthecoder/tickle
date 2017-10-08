package uk.co.nickthecoder.tickle

import org.joml.Matrix4f
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.util.Angle

private var nextId: Int = 0

class Actor(val role: Role? = null) {

    val id = nextId++

    internal var stage: Stage? = null

    val position = Vector2d(0.0, 0.0)

    /**
     * Gets or sets the x value of position
     */
    var x: Double
        get() = position.x
        set(v) {
            position.x = v
        }

    var y: Double
        get() = position.y
        set(v) {
            position.y = v
        }

    var z: Double = 0.0

    val direction: Angle = object : Angle() {
        override var radians = 0.0
            set(v) {
                if (field != v) {
                    field = v
                    dirtyMatrix = true
                }
            }
    }

    var scale: Double = 1.0
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

    /**
     * Return false iff the actor requires special transformations to render it.
     */
    fun isSimpleImage(): Boolean =
            direction.radians == appearance.directionRadians && scale == 1.0 && !flipX && !flipY && customTransformation == null


    fun getModelMatrix(): Matrix4f {
        if (dirtyMatrix) {
            modelMatrix.identity().translate(x.toFloat(), y.toFloat(), 0f)
            if (direction.radians != 0.0) {
                modelMatrix.rotateZ((direction.radians - appearance.directionRadians).toFloat())
            }
            if (scale != 1.0) {
                modelMatrix.scale(scale.toFloat())
            }
            if (flipX) {
                modelMatrix.reflect(1f, 0f, 0f, 0f)
            }
            if (flipY) {
                modelMatrix.reflect(0f, 1f, 0f, 0f)
            }
            customTransformation?.let {
                modelMatrix.mul(it)
            }
            modelMatrix.translate(-x.toFloat(), -y.toFloat(), 0f)
        }
        return modelMatrix
    }

    fun changePose(pose: Pose) {
        appearance = PoseAppearance(this, pose)
    }

    var flipX: Boolean = false
        set(v) {
            if (field != v) {
                field = v
                dirtyMatrix = true
            }
        }


    var flipY: Boolean = false
        set(v) {
            if (field != v) {
                field = v
                dirtyMatrix = true
            }
        }

    var customTransformation: Matrix4f? = null
        set(v) {
            if (field !== v) {
                field = v
                dirtyMatrix
            }
        }

    fun die() {
        stage?.remove(this)
        role?.end()
    }

    override fun toString() = "Actor #$id @ $x,$y Role=${role?.javaClass?.simpleName ?: "<none>"}"
}

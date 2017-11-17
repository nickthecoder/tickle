package uk.co.nickthecoder.tickle

import org.jbox2d.dynamics.Body
import org.joml.Matrix4f
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.resources.ActorXAlignment
import uk.co.nickthecoder.tickle.resources.ActorYAlignment
import uk.co.nickthecoder.tickle.sound.SoundManager
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.util.Angle

private var nextId: Int = 0

class Actor(var costume: Costume, val role: Role? = null) {

    val id = nextId++

    var stage: Stage? = null

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

    var zOrder: Double = 0.0

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

    var color: Color = Color.white()

    var appearance: Appearance = InvisibleAppearance()

    private val modelMatrix = Matrix4f()

    private var dirtyMatrix: Boolean = false

    var body: Body? = null

    val textAppearance: TextAppearance?
        get() {
            val app = appearance
            if (app is TextAppearance) {
                return app
            }
            return null
        }

    val poseAppearance: PoseAppearance?
        get() {
            val app = appearance
            if (app is PoseAppearance) {
                return app
            }
            return null
        }

    var xAlignment: ActorXAlignment = ActorXAlignment.LEFT
    var yAlignment: ActorYAlignment = ActorYAlignment.BOTTOM

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

    fun changeAppearance(pose: Pose) {
        val a = appearance
        if (a is PoseAppearance) {
            a.pose = pose
        } else {
            appearance = PoseAppearance(this, pose)
        }
    }

    fun changeAppearance(text: String, textStyle: TextStyle) {
        appearance = TextAppearance(this, text, textStyle)
    }

    fun hide() {
        appearance = InvisibleAppearance()
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

    fun event(name: String) {
        val pose = costume.choosePose(name)
        if (pose == null) {

            val newText = costume.chooseString(name) ?: textAppearance?.text
            val textStyle = costume.chooseTextStyle(name) ?: textAppearance?.textStyle
            if (newText != null && textStyle != null) {
                changeAppearance(newText, textStyle)
            }

        } else {
            changeAppearance(pose)
        }

        costume.chooseSound(name)?.let { sound ->
            SoundManager.play(sound)
        }
    }

    fun createChild(eventName: String): Actor {
        val childActor = costume.createChild(eventName)
        childActor.x = x
        childActor.y = y
        Game.instance.scene.world?.let { world ->
            childActor.body?.setTransform(world.pixelsToWorld(childActor.position), childActor.direction.radians.toFloat())
        }

        return childActor
    }

    fun createChildOnStage(eventName: String): Actor {
        val actor = createChild(eventName)
        stage?.add(actor)
        return actor
    }

    fun die() {
        role?.end()
        body?.let {
            Game.instance.scene.world?.destroyBody(it)
            body = null
        }
        stage?.remove(this)
    }

    /**
     * Is the point within the actor's rectangular region? Note, this uses a rectangle not aligned
     * with the x/y axis, and is therefore still useful when the actor is rotated.
     */
    fun contains(vector: Vector2d) = appearance.contains(vector)

    /**
     * For a PoseAppearance, is the vector non-transparent pixel of the pose.
     * For a TextAppearance, is the vector within the bounding rectangle of the text (note uses a rectangle not aligned
     * with the x/y axis, and is therefore still useful when the actor is rotated).
     */
    fun touching(vector: Vector2d) = appearance.touching(vector)

    override fun toString() = "Actor #$id @ $x,$y Role=${role?.javaClass?.simpleName ?: "<none>"}"
}

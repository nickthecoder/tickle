package uk.co.nickthecoder.tickle

import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.joml.Matrix4f
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.physics.TickleWorld
import uk.co.nickthecoder.tickle.physics.scale
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
     * Used when the Actor has a [Body] to detect when the position has changed by game code, and therefore the Body
     * needs to be updated.
     */
    private val oldPosition = Vector2d(0.0, 0.0)

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
                    // If the Actor has a Body, this ensures that the body will be updated before JBox2D's calculations
                    // are applied.
                    oldPosition.x = Double.MIN_VALUE
                }
            }
    }

    var scale = Vector2d(1.0, 1.0)

    /**
     * A convenience, setting scale.x and scale.y to the same value.
     * When getting, if scale.x and scale.y differ, then only scale.x is returned.
     */
    var scaleXY: Double
        get() = scale.x
        set(v) {
            scale.x = v
            scale.y = v
        }

    private val oldScale = Vector2d(1.0, 1.0)


    var color: Color = Color.white()

    var appearance: Appearance = InvisibleAppearance()

    private val modelMatrix = Matrix4f()

    private var dirtyMatrix: Boolean = true
        get() {
            return field || position != oldPosition || oldScale != scale
        }

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
    internal fun isSimpleImage(): Boolean =
            direction.radians == appearance.directionRadians && scale.x == 1.0 && scale.y == 1.0 && !flipX && !flipY && customTransformation == null

    internal fun calculateModelMatrix(): Matrix4f {
        if (dirtyMatrix) {
            recalculateModelMatrix()
        }
        return modelMatrix
    }

    private fun recalculateModelMatrix() {
        modelMatrix.identity().translate(x.toFloat(), y.toFloat(), 0f)
        if (direction.radians != 0.0) {
            modelMatrix.rotateZ((direction.radians - appearance.directionRadians).toFloat())
        }
        if (scale.x != 1.0 || scale.y != 1.0) {
            modelMatrix.scale(scale.x.toFloat(), scale.y.toFloat(), 1f)
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

        body?.let { body ->
            // We need to
            if (oldScale != scale) {
                body.scale((scale.x / oldScale.x).toFloat(), (scale.y / oldScale.y).toFloat())
                oldScale.set(scale)
                oldPosition.set(position)
                // TODO Is this needed?
                updateBody()
            }
            if (oldPosition != position) {
                updateBody()
                oldPosition.set(position)
            }
        }

        dirtyMatrix = false
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

    internal var customTransformation: Matrix4f? = null
        set(v) {
            if (field !== v) {
                field = v
                dirtyMatrix = true
            }
        }

    fun resize(width: Double, height: Double) {
        appearance.resize(width, height)
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
        updateBody()

        return childActor
    }

    fun createChildOnStage(eventName: String): Actor {
        val childActor = createChild(eventName)
        stage?.add(childActor)
        return childActor
    }

    fun createChild(costume: Costume, eventName: String = "default"): Actor {
        val childActor = costume.createActor(eventName)
        childActor.x = x
        childActor.y = y
        updateBody()
        return childActor
    }

    fun createChildOnStage(costume: Costume, eventName: String = "default"): Actor {
        val childActor = createChild(costume, eventName)
        stage?.add(childActor)
        return childActor
    }

    fun die() {
        role?.end()
        body?.let { body ->
            body.world.destroyBody(body)
            this.body = null
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

    /**
     * Directly changes the position and the angle of the body. Note, this can cause strange behaviour if the body
     * overlaps another body.
     */
    internal fun updateBody() {
        body?.let { body ->
            val world = body.world as TickleWorld
            world.pixelsToWorld(tempVec, position)
            body.setTransform(tempVec, (direction.radians - (poseAppearance?.directionRadians ?: 0.0)).toFloat())
        }
    }

    internal fun updateFromBody(world: TickleWorld) {
        body?.let { body ->
            world.worldToPixels(position, body.position)
            direction.radians = body.angle.toDouble() + (poseAppearance?.directionRadians ?: 0.0)
            // Copy the Actor's position, so that we can test is game code has changed the position, and therefore
            // we will know if the Body needs to be updated. See ensureBodyIsUpToDate.
            oldPosition.set(position)
        }
    }

    internal fun ensureBodyIsUpToDate() {
        body?.let {
            // olsPosition and oldScale are both used for two purposes. To know when the body is out of date, and also to know when the
            // modelMatrix is out of date. So, recalculate the mode matrix, which will also sync the body.
            if (dirtyMatrix) {
                recalculateModelMatrix()
            }
        }
    }

    private val tempVec: Vec2 by lazy { Vec2() }

    override fun toString() = "Actor #$id @ $x,$y Role=${role?.javaClass?.simpleName ?: "<none>"}"

}

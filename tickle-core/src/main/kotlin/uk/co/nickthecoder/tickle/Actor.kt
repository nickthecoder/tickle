/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle

import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.joml.Matrix4f
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.physics.TickleBody
import uk.co.nickthecoder.tickle.physics.TickleWorld
import uk.co.nickthecoder.tickle.physics.scale
import uk.co.nickthecoder.tickle.resources.ActorXAlignment
import uk.co.nickthecoder.tickle.resources.ActorYAlignment
import uk.co.nickthecoder.tickle.sound.SoundManager
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.util.Angle

private var nextId: Int = 0

class Actor(var costume: Costume, role: Role? = null)

    : ActorDetails {

    val id = nextId++

    /**
     * The role defines the behaviour of this Actor (how it moves etc).
     *
     * Usually, an actor keeps the same Role forever.
     * However, in rare cases, the role of an Actor can be changed.
     * If you do change the actor's Role, ensure that the role has not been used by another Actor,
     * otherwise bad things will happen!
     *
     * You may re-use a role (that was previously assigned to the same actor). Note, the role's
     * [Role.begin] and [Role.activated] methods will be called each time the role is changed.
     * So if you want to run code only once, then you'll need to set a flag, and have an "if"
     * in your [Role.begin] and [Role.activated] methods.
     */
    var role: Role? = role
        set(v) {
            field?.end()
            field = v
            v?.actor = this
            if (stage != null) {
                v?.begin()
                v?.activated()
            }
        }

    var stage: Stage? = null

    val position = Vector2d(0.0, 0.0)

    /**
     * Used when the Actor has a [Body] to detect when the position has changed by game code, and therefore the Body
     * needs to be updated.
     */
    private val oldPosition = Vector2d(Double.MIN_VALUE, Double.MAX_VALUE)

    /**
     * Gets or sets the x value of position
     */
    override var x: Double
        get() = position.x
        set(v) {
            position.x = v
        }

    override var y: Double
        get() = position.y
        set(v) {
            position.y = v
        }

    override var zOrder: Double = 0.0

    var direction: Angle = object : Angle() {
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

    /**
     * Scales the Actor, if this Actor the JBox2d physics engine, then the shapes and the joints between them will
     * also be scaled. However, if the actor is scaled by different x and y values, and circular shapes are used,
     * then these will be converted to polygons, because JBox2d doesn't support ellipses.
     * So make sure that you only scale by scalars (equal x and y values) if this would cause problems
     * (polygons are slower than circles, and polygons won't roll as nicely due to their pointy nature).
     * Also note that once a circle has been converted to a polygon, resetting the scale will NOT return to
     * using circles.
     */
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

    internal val modelMatrix = Matrix4f()

    private var dirtyMatrix: Boolean = true
        get() {
            return field || position != oldPosition || oldScale != scale
        }

    var body: TickleBody? = null

    val textAppearance: TextAppearance?
        get() {
            val app = appearance
            if (app is TextAppearance) {
                return app
            }
            return null
        }

    val ninePatchAppearance: NinePatchAppearance?
        get() {
            val app = appearance
            if (app is NinePatchAppearance) {
                return app
            }
            return null
        }

    val tiledAppearance: TiledAppearance?
        get() {
            val app = appearance
            if (app is TiledAppearance) {
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

    var viewAlignmentX: ActorXAlignment = ActorXAlignment.LEFT
    var viewAlignmentY: ActorYAlignment = ActorYAlignment.BOTTOM

    init {
        role?.actor = this
    }

    /**
     * Return false iff the actor requires special transformations to render it.
     */
    internal fun isSimpleImage(): Boolean =
            direction.radians == appearance.directionRadians && scale.x == 1.0 && scale.y == 1.0

    internal fun calculateModelMatrix(): Matrix4f {
        if (dirtyMatrix) {
            recalculateModelMatrix()

            body?.let { body ->
                if (oldScale != scale) {
                    body.jBox2DBody.scale((scale.x / oldScale.x).toFloat(), (scale.y / oldScale.y).toFloat())
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
        return modelMatrix
    }

    private fun recalculateModelMatrix() {
        modelMatrix.identity().translate(x.toFloat(), y.toFloat(), 0f)
        if (direction.radians != appearance.directionRadians) {
            modelMatrix.rotateZ((direction.radians - appearance.directionRadians).toFloat())
        }
        if (scale.x != 1.0 || scale.y != 1.0) {
            modelMatrix.scale(scale.x.toFloat(), scale.y.toFloat(), 1f)
        }
        modelMatrix.translate(-x.toFloat(), -y.toFloat(), 0f)
    }

    fun resize(width: Double, height: Double) {
        appearance.resize(width, height)
    }

    fun width() = appearance.width()

    fun height() = appearance.height()

    fun changeAppearance(pose: Pose) {
        if (pose.tiled) {
            val tiled = TiledAppearance(this, pose)
            tiled.resize(pose.rect.width.toDouble(), pose.rect.height.toDouble())
            appearance = tiled
        } else {
            val a = appearance
            if (a is PoseAppearance) {
                a.pose = pose
            } else {
                appearance = PoseAppearance(this, pose)
            }
        }
    }

    fun changeAppearance(ninePatch: NinePatch) {
        val oldAppearance = appearance
        val newAppearance = NinePatchAppearance(this, ninePatch)
        appearance = newAppearance
        if (oldAppearance is ResizeAppearance) {
            newAppearance.oldSize.set(oldAppearance.oldSize)
            newAppearance.size.set(oldAppearance.size)
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
            } else {

                val ninePatch = costume.chooseNinePatch(name)
                if (ninePatch != null) {
                    changeAppearance(ninePatch)
                }

            }

        } else {
            changeAppearance(pose)
        }

        costume.chooseSound(name)?.let { sound ->
            SoundManager.play(sound)
        }
    }

    /**
     * Creates a new Actor, whose Costume is defined by looking at this Actor's events of type "Costume".
     * The role for the new Actor is defined by the new Actor's Costume.
     *
     * The new Actor is placed on the same Stage, and at the same position as this Actor.
     *
     * The new Actor is placed on the same stage as this Actor.
     */
    fun createChild(eventName: String): Actor {
        val childActor = costume.createChild(eventName)
        childActor.x = x
        childActor.y = y
        updateBody()

        stage?.add(childActor)
        return childActor
    }

    /**
     * Creates a new Actor, using the Costume provided.
     * The role for the new Actor is defined by the [costume].
     *
     * The new Actor is placed on the same Stage, and at the same position as this Actor.
     */
    fun createChild(costume: Costume): Actor {
        val childActor = costume.createActor()
        childActor.x = x
        childActor.y = y
        updateBody()

        stage?.add(childActor)
        return childActor
    }

    /**
     * Calls [Role.end], and removes the actor from the [Stage].
     *
     * If you want to test if an Actor is dead, the best you can do is check if [stage] == null.
     * However, this isn't perfect, because it is possible for an Actor to be removed from a Stage
     * without the [Role.end] being called.
     */
    fun die() {
        role?.end()
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
            val world = body.tickleWorld
            world.pixelsToWorld(tempVec, position)
            body.jBox2DBody.setTransform(tempVec, (direction.radians - (appearance.directionRadians)).toFloat())
        }
    }

    internal fun updateFromBody(world: TickleWorld) {
        body?.let { body ->
            world.worldToPixels(position, body.jBox2DBody.position)
            direction.radians = body.jBox2DBody.angle.toDouble() + (appearance.directionRadians ?: 0.0)
            // Copy the Actor's position, so that we can test if game code has changed the position, and therefore
            // we will know if the Body needs to be updated. See ensureBodyIsUpToDate.
            oldPosition.set(position)
        }
    }

    fun ensureBodyIsUpToDate() {
        body?.let {
            // NinePatchAppearance and TiledAppearance need to scale and/or change the offsets of the fixtures
            // if the size or alignment has changed.
            appearance.updateBody()

            // oldPosition and oldScale are both used for two purposes :
            // 1) To know when the body is out of date
            // 2) To know when the modelMatrix is out of date.
            // So, recalculate the mode matrix, which will also sync the body.
            calculateModelMatrix()
        }
    }

    private val tempVec: Vec2 by lazy { Vec2() }

    fun moveForwards(amount: Double) {
        position.add(direction.vector().mul(amount))
    }

    fun moveSidewards(amount: Double) {
        position.add(direction.vector().perpendicular().mul(amount))
    }

    override fun toString() = "Actor #$id @ $x,$y Role=${role?.javaClass?.simpleName ?: "<none>"}"

}

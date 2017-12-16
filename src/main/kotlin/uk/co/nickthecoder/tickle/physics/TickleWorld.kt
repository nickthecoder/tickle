package uk.co.nickthecoder.tickle.physics

import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.resources.Resources

fun worldScale() = Game.instance.scene.world?.scale ?: Resources.instance.gameInfo.physicsInfo.scale.toFloat()

fun pixelsToWorld(pixels: Double) = pixels.toFloat() / worldScale()

fun worldToPixels(world: Float) = (world * worldScale()).toDouble()

fun pixelsToWorld(vector2d: Vector2d) = Vec2(pixelsToWorld(vector2d.x), pixelsToWorld(vector2d.y))

fun worldToPixels(vec2: Vec2) = Vector2d(worldToPixels(vec2.x), worldToPixels(vec2.y))

fun pixelsToWorld(vec2: Vec2, vector2d: Vector2d) {
    vec2.x = pixelsToWorld(vector2d.x)
    vec2.y = pixelsToWorld(vector2d.y)
}

fun worldToPixels(vector2d: Vector2d, vec2: Vec2) {
    vector2d.x = worldToPixels(vec2.x)
    vector2d.y = worldToPixels(vec2.y)
}

fun BodyType.hasFixtures() = this == BodyType.DYNAMIC || this == BodyType.STATIC

class TickleWorld(
        gravity: Vector2d = Vector2d(0.0, 0.0),
        val scale: Float = 100f,
        val velocityIterations: Int = 8,
        val positionIterations: Int = 3)

    : World(pixelsToWorld(gravity), true) {

    val maxTimeStep = 1.0 / 30.0 // 30 frames per second

    /** If the time step interval is more than maxTimeStep, should multiple steps be calculated, or just one?
     * When true, a single step is calculated, and the remaining time is ignored.
     * When false, multiple steps are calculated to fill the actual elapsed time. This will be more acurate, but
     * will take more time.
     */
    val truncate = false

    val tempVec = Vec2()

    fun pixelsToWorld(pixels: Double) = pixels.toFloat() / scale

    fun worldToPixels(world: Float) = (world * scale).toDouble()

    fun pixelsToWorld(vector2d: Vector2d) = Vec2(pixelsToWorld(vector2d.x), pixelsToWorld(vector2d.y))

    fun worldToPixels(vec2: Vec2) = Vector2d(worldToPixels(vec2.x), worldToPixels(vec2.y))

    fun pixelsToWorld(vec2: Vec2, vector2d: Vector2d) {
        vec2.x = pixelsToWorld(vector2d.x)
        vec2.y = pixelsToWorld(vector2d.y)
    }

    fun worldToPixels(vector2d: Vector2d, vec2: Vec2) {
        vector2d.x = worldToPixels(vec2.x)
        vector2d.y = worldToPixels(vec2.y)
    }

    fun createBody(bodyDef: TickleBodyDef, actor: Actor): Body {
        bodyDef.position = pixelsToWorld(actor.position)
        bodyDef.angle = actor.direction.radians.toFloat()

        val body = createBody(bodyDef)
        bodyDef.fixtureDefs.forEach { fixtureDef ->
            val shape = fixtureDef.shapeDef.createShape(this)
            fixtureDef.shape = shape
            body.createFixture(fixtureDef)

        }
        actor.body = body
        body.userData = actor
        return body
    }

    fun tick() {
        // First make sure that the body is up to date. If the Actor's position or direction have been changed by game code,
        // then the Body will need to be updated before we can call "step".
        var body = bodyList
        while (body != null) {
            val actor = body.userData
            if (actor is Actor) {
                actor.ensureBodyIsUpToDate()
            }
            body = body.next
        }

        // Perform all of the JBox2D calculations
        var interval = Game.instance.tickDuration
        if (interval > maxTimeStep && truncate) {
            interval = maxTimeStep
        }
        while (interval > 0.0) {
            step(Math.min(interval, maxTimeStep).toFloat(), velocityIterations, positionIterations)
            interval -= maxTimeStep
        }

        // Update the actor's positions and directions
        body = bodyList
        while (body != null) {
            val actor = body.userData
            if (actor is Actor) {
                actor.updateFromBody(this)
            }
            body = body.next
        }
    }

    fun addContactListener(contactListener: ContactListener) {
        val existingListener = m_contactManager.m_contactListener
        if (existingListener == null) {
            setContactListener(contactListener)
        } else if (existingListener is CompoundContactListener) {
            existingListener.listeners.add(contactListener)
        } else {
            val compound = CompoundContactListener()
            compound.listeners.add(existingListener)
            compound.listeners.add(contactListener)
            setContactListener(compound)
        }
    }

    fun removeContactListener(contactListener: ContactListener) {
        val existingListener = m_contactManager.m_contactListener
        if (existingListener === contactListener) {
            setContactListener(null)
        } else if (existingListener is CompoundContactListener) {
            existingListener.listeners.remove(contactListener)
            if (existingListener.listeners.isEmpty()) {
                setContactListener(null)
            }
        }
    }

}

fun Body.offset(dx: Float, dy: Float) {

    // We cannot just iterate through the fixtures, because we need to delete the old
    // ones and create new ones. So get all the fixtures first...
    val fixtures = mutableListOf<Fixture>()
    var fixture = fixtureList
    while (fixture != null) {
        fixtures.add(fixture)
        fixture = fixture.next
    }
    fixtures.forEach { it.offset(this, dx, dy) }
    resetMassData()
    isAwake = true
}


fun Fixture.offset(body: Body, dx: Float, dy: Float) {
    val shape = shape

    var newShape: Shape? = null

    when (shape) {
        is CircleShape -> {
            shape.m_p.x += dx
            shape.m_p.y += dy
        }
        is PolygonShape -> {
            val points = Array<Vec2>(shape.vertexCount) { Vec2(shape.vertices[it].x + dx, shape.vertices[it].y + dy) }

            newShape = PolygonShape()
            newShape.set(points, shape.vertexCount)
        }
    }

    if (newShape != null) {
        val fixtureDef = FixtureDef()
        fixtureDef.filter = m_filter
        fixtureDef.friction = friction
        fixtureDef.density = density
        fixtureDef.restitution = restitution
        fixtureDef.isSensor = isSensor
        fixtureDef.userData = userData
        fixtureDef.shape = newShape
        body.destroyFixture(this)
        body.createFixture(fixtureDef)
    }
}

fun Body.scale(scaleX: Float, scaleY: Float) {

    // We cannot just iterate through the fixtures, because to scale the fixtures, we need to delete the old
    // ones and create new ones. So get all the fixtures first...
    val fixtures = mutableListOf<Fixture>()
    var fixture = fixtureList
    while (fixture != null) {
        fixtures.add(fixture)
        fixture = fixture.next
    }
    fixtures.forEach { it.scale(this, scaleX, scaleY) }
    resetMassData()
    isAwake = true
}

fun Fixture.scale(body: Body, scaleX: Float, scaleY: Float) {
    val shape = shape

    var newShape: Shape? = null

    when (shape) {
        is CircleShape -> {
            shape.m_radius *= scaleX
            shape.m_p.x *= scaleX
            shape.m_p.y *= scaleY
        }
        is PolygonShape -> {
            val points = Array<Vec2>(shape.vertexCount) { Vec2(shape.vertices[it].x * scaleX, shape.vertices[it].y * scaleY) }

            newShape = PolygonShape()
            newShape.set(points, shape.vertexCount)
        }
    }

    if (newShape != null) {
        val fixtureDef = FixtureDef()
        fixtureDef.filter = m_filter
        fixtureDef.friction = friction
        fixtureDef.density = density
        fixtureDef.restitution = restitution
        fixtureDef.isSensor = isSensor
        fixtureDef.userData = userData
        fixtureDef.shape = newShape
        body.destroyFixture(this)
        body.createFixture(fixtureDef)
    }
}

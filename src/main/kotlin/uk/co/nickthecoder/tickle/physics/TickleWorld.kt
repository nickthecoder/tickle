package uk.co.nickthecoder.tickle.physics

import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.World
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
            fixtureDef.shapeDef.createShapes(this).forEach { shape ->
                fixtureDef.shape = shape
                body.createFixture(fixtureDef)
            }
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
        step(Game.instance.tickDuration.toFloat(), velocityIterations, positionIterations)

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

}

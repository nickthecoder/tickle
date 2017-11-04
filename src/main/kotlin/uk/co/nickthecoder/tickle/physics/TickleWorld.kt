package uk.co.nickthecoder.tickle.physics

import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game

class TickleWorld(
        gravity: Vector2d = Vector2d(0.0, 0.0),
        val scale: Float = 100f,
        val velocityIterations: Int = 8,
        val positionIterations: Int = 3) {

    val world = World(pixelsToWorld(gravity), true)

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

    fun createBody(def: CostumeBodyDef, actor: Actor): Body {
        val bodyDef = BodyDef()
        bodyDef.type = def.bodyType

        bodyDef.position = pixelsToWorld(actor.position)
        bodyDef.angle = actor.direction.radians.toFloat()

        def.updateShapes(this)
        val body = world.createBody(bodyDef)
        body.userData = actor
        return body
    }

    fun tick() {
        world.step(Game.instance.tickDuration.toFloat(), velocityIterations, positionIterations)
        var body = world.bodyList
        while (body != null) {
            body = body.next
            val actor = body.userData
            if (actor is Actor) {
                worldToPixels(actor.position, body.position)
                actor.direction.radians = body.angle.toDouble()
            }
        }
    }

}

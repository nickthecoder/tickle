package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.joml.Vector2d

/**
 * Holds details of a Costume's body, where all values are in Tickle's coordinate system.
 * Box2D explicitly states that values should NOT be based on pixels, because it is optimised for
 * objects of size around 10, not hundreds.
 */
class CostumeBodyDef {

    var bodyType: BodyType = BodyType.DYNAMIC
    val fixtures = mutableListOf<CostumeFixtureDef>()

    fun createBodyDef(position: Vector2d): BodyDef {
        val bodyDef = BodyDef()
        bodyDef.type = bodyType

        return bodyDef
    }

    fun updateShapes(world: TickleWorld) {
        fixtures.forEach { fixture ->
            fixture.shape = fixture.shapeDef.createShape(world)
        }
    }
}

package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.BodyType

/**
 * Holds details of a Costume's body, where all values are in Tickle's coordinate system.
 * Box2D explicitly states that values should NOT be based on pixels, because it is optimised for
 * objects of size around 10, not hundreds.
 */
class CostumeBodyDef {

    var bodyType: BodyType = BodyType.DYNAMIC
    val fixtureDefs = mutableListOf<CostumeFixtureDef>()

    fun updateShapes(world: TickleWorld) {
        fixtureDefs.forEach { fixtureDef ->
            fixtureDef.shape = fixtureDef.shapeDef.createShape(world)
        }
    }
}

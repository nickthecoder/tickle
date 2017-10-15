package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.TaggedRole
import uk.co.nickthecoder.tickle.neighbourhood.Occupant
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Rectd
import uk.co.nickthecoder.tickle.util.Tagged

val ROOM_WIDTH = 8000.0
val ROOM_HEIGHT = 8000.0

abstract class Bounce : AbstractRole(), TaggedRole {

    val radius = 30.0

    override val tagged = Tagged(Game.instance.director.tagManager, this)

    @Attribute(AttributeType.RELATIVE_POSITION, scale = 10.0)
    var velocity = Vector2d()

    override fun activated() {
        tagged.add(DemoTags.BOUNCY)
    }


    override fun tick() {
        actor.position.add(velocity)

        // Bounce at walls
        if (actor.x < 0.0 && velocity.x < 0.0) {
            velocity.x = -velocity.x
        }
        if (actor.y < 0.0 && velocity.y < 0.0) {
            velocity.y = -velocity.y
        }
        if (actor.x > ROOM_WIDTH && velocity.x > 0.0) {
            velocity.x = -velocity.x
        }
        if (actor.y > ROOM_HEIGHT && velocity.y > 0.0) {
            velocity.y = -velocity.y
        }
    }

    fun isOverlapping(other: Bounce): Boolean {

        if (other === this) return false
        return actor.position.distanceSquared(other.actor.position) < (radius + other.radius) * (radius + other.radius)
    }

    override fun toString() = "Bounce @ ${actor.position.x},${actor.position.x} V=${velocity.x},${velocity.y}"

}


class NoBounce : Bounce() {

    override fun tick() {
        super.tick()
    }

}

class SimpleBounce : Bounce() {

    override fun tick() {
        super.tick()
        /*
         * This is a O(n*n) - very bad! When the number of coins reaches about 1,300, my crappy old laptop starts to
         * drop frames. i.e. when isOverlapping is being called about 1.7 million times every frame.
         */

        tagged.findRoles(DemoTags.BOUNCY).forEach { other ->
            if (other is SimpleBounce && isOverlapping(other)) {
                collide(actor.position, velocity, 1.0,
                        other.actor.position, other.velocity, 1.0)
            }
        }

    }

}

class NeighbourhoodBounce : Bounce() {

    val occupant = Occupant(Game.instance.director.neighbourhood, this)

    private val worldRect = Rectd()

    override fun tick() {
        super.tick()

        worldRect.left = actor.x
        worldRect.bottom = actor.y
        worldRect.right = actor.x + 60.0
        worldRect.top = actor.y + 60.0

        occupant.update(worldRect)

        occupant.neighbours().forEach { neighbour ->
            val other = neighbour.role
            if (other is NeighbourhoodBounce && isOverlapping(other)) {
                collide(actor.position, velocity, 1.0,
                        other.actor.position, other.velocity, 1.0)
            }
        }

    }
}

private val distance = Vector2d()

fun Vector2d.distanceSquared(other: Vector2d): Double {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}

fun collide(
        positionA: Vector2d, velocityA: Vector2d, massA: Double,
        positionB: Vector2d, velocityB: Vector2d, massB: Double) {

    val dx = positionA.x - positionB.x
    val dy = positionA.y - positionB.y

    val dist = Math.sqrt(dx * dx + dy * dy)

    val dvx = velocityB.x - velocityA.x
    val dvy = velocityB.y - velocityA.y

    // The speed of the collision in the direction of the line between their centres.
    val collision = (dvx * dx + dvy * dy) / dist

    if (collision < 0) {
        // They are moving away from each other
        return
    }

    val massSum = massA + massB

    velocityA.x += dx / dist * collision * 2.0 * massB / massSum
    velocityB.x -= dx / dist * collision * 2.0 * massA / massSum

    velocityA.y += dy / dist * collision * 2.0 * massB / massSum
    velocityB.y -= dy / dist * collision * 2.0 * massB / massSum

}

package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.neighbourhood.Occupant
import uk.co.nickthecoder.tickle.stage.findRoles
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Rectd
import uk.co.nickthecoder.tickle.util.Tagged
import uk.co.nickthecoder.tickle.util.TaggedRole

val ROOM_WIDTH = 8000.0
val ROOM_HEIGHT = 8000.0

/**
 * Tests various optimisations. A Bee can ejects coins, which bounce around the screen, colliding with each other.
 *
 * NoBounce performs no collisions, and therefore is O(n), as each still needs to be moved, and rendered.
 *
 * With unoptimised code, the algorithm is O(n*n) because each object must check all other objects for collisions.
 *
 * Using NeighbourhoodBounce, the order is closer to O(n), when the world is sparsely occupied (i.e. when each block only
 * has one coin in it).
 *
 * We can also perform the collisions using JBox2D, which I assume has some internal optimisation for proximity tests
 * similar to Neighbourhood. However, the physics of JBox2D is more complex (and also more accurate), and therefore
 * I don't expect it to perform as well as NeighbourhoodBounce. Also, the proximity tests in JBox2D cannot be so highly
 * tuned as NeighbourhoodBounce (which has a perfect Block size for this particular test). In a real game, Neighbourhood
 * cannot be so perfectly tuned.
 *
 * The classes TaggedBounce and FindBounce are both O(n*n) with respect to collision detection. They differ by the order
 * of *finding* the other objects with which to collide.
 *
 * With respect to *finding* the other objects, TaggedBounce is O(n*n) where n is the number of TaggedBounce objects.
 * However, FindBounce is O(n*n + n*m) where n is the number of FindBounce objects, and m is the number of
 * non-FindBounce objects.
 *
 * Therefore if we flood the scene with lots of NoBounce objects (m), then FindBounce will still be slow for a small
 * number of FindBounce objects (n).
 * However, if we change the StageView to use OptimisedStage, then FindBounce will become O(n*n) i.e. independent of m.
 * Therefore the number of NoBounce objects will NOT affect performance. So we can expect thousands of NoBounce and
 * hundreds of FindBounce to perform well ONLY when OptimisedStage is used. It will perform poorly when GameStage is used.
 *
 * To test FindBounce, create 200 FindBounce objects, then create NoBounce objects till frames are dropped.
 * Do the same again, but change the Stage from GameStage to OptimisedStage.
 *
 * Testing the others is simpler, as you only have to create objects of one type, and the type of Stage does not matter.
 */
abstract class Bounce : AbstractRole() {

    val radius = 30.0

    @Attribute(AttributeType.RELATIVE_POSITION, scale = 10.0)
    var velocity = Vector2d()

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

class FindBounce : Bounce() {

    override fun tick() {
        super.tick()

        /*
         * This is a O(n*n) - very bad! When the number of coins reaches about 1,300, my crappy old laptop starts to
         * drop frames. i.e. when isOverlapping is being called about 1.7 million times every frame.
         */
        actor.stage?.findRoles<Bounce>()?.forEach { other ->
            if (other is FindBounce && isOverlapping(other)) {
                collide(actor.position, velocity, 1.0,
                        other.actor.position, other.velocity, 1.0)
            }
        }

    }

}

class TaggedBounce : Bounce(), TaggedRole {

    override lateinit var tagged: Tagged

    override fun activated() {
        tagged = Tagged(Play.instance.tagManager, this)
        tagged.add(DemoTags.BOUNCY)
    }

    override fun tick() {
        super.tick()

        /*
         * This is a O(n*n) - very bad! When the number of coins reaches about 1,300, my crappy old laptop starts to
         * drop frames. i.e. when isOverlapping is being called about 1.7 million times every frame.
         */
        tagged.findRoles(DemoTags.BOUNCY).forEach { other ->
            if (other is TaggedBounce && isOverlapping(other)) {
                collide(actor.position, velocity, 1.0,
                        other.actor.position, other.velocity, 1.0)
            }
        }

    }

}

class NeighbourhoodBounce : Bounce() {

    lateinit var occupant: Occupant

    private val worldRect = Rectd()

    override fun activated() {
        super.activated()
        occupant = Occupant(Play.instance.neighbourhood, this)
    }

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

class WorldBound : AbstractRole() {
    override fun tick() {
        actor.body?.let { body ->
            if (body.position.x < 0f && body.linearVelocity.x < 0) {
                body.linearVelocity.x = -body.linearVelocity.x
            }
            if (body.position.y < 0f && body.linearVelocity.y < 0) {
                body.linearVelocity.y = -body.linearVelocity.y
            }
            if (body.position.x > 10f && body.linearVelocity.x > 0) {
                body.linearVelocity.x = -body.linearVelocity.x
            }
            if (body.position.y > 10f && body.linearVelocity.y > 0) {
                body.linearVelocity.y = -body.linearVelocity.y
            }
        }
        // println("Actor ${actor}. Body ${actor.body}. Position ${actor.body?.position}")
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

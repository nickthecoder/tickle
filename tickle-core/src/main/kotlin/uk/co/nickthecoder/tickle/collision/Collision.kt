package uk.co.nickthecoder.tickle.collision

import org.joml.Vector2d

fun Vector2d.distanceSquared(other: Vector2d): Double {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}

fun circularCollision(
        positionA: Vector2d, velocityA: Vector2d, massA: Double = 1.0,
        positionB: Vector2d, velocityB: Vector2d, massB: Double = 1.0) {

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

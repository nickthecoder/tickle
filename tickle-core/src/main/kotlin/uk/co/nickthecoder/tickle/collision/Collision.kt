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

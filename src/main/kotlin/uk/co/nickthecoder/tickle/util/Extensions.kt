package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d

fun Vector2d.distanceSquared(other: Vector2d) = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)

fun Vector2d.rotate(angle: Angle) = rotate(angle.radians)

fun Vector2d.rotate(radians: Double) {
    val sin = Math.sin(radians)
    val cos = Math.cos(radians)
    val rx = x * sin - y * cos
    val ry = y * sin + x * cos
    set(rx, ry)
}

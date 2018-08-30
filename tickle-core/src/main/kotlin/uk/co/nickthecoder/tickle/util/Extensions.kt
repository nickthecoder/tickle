package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d

fun Vector2d.distanceSquared(other: Vector2d) = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)

fun Vector2d.rotate(angle: Angle) = rotate(angle.radians)

fun Vector2d.rotate(radians: Double) {
    val sin = Math.sin(radians)
    val cos = Math.cos(radians)
    val rx = x * cos - y * sin
    val ry = y * cos + x * sin
    set(rx, ry)
}

fun Vector2d.radians() = Math.atan2(y, x)

fun Vector2d.degrees() = Math.toDegrees(Math.atan2(y, x))

fun Vector2d.string() = "( $x , $y )"

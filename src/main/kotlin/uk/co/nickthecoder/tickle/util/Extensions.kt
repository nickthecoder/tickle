package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d

fun Vector2d.distanceSquared(other: Vector2d) = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)

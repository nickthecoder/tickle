package uk.co.nickthecoder.tickle

import org.joml.Vector2d

class PhysicsInfo {
    var gravity: Vector2d = Vector2d(0.0, 0.0)
    var velocityIterations: Int = 8
    var positionIterations: Int = 3
    var scale: Double = 100.0
}

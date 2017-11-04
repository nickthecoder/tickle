package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.Shape
import org.joml.Vector2d

class CircleDef(
        var center: Vector2d,
        var radius: Double = 1.0)

    : ShapeDef {

    override fun createShape(world: TickleWorld): Shape {
        val circle = CircleShape()
        world.pixelsToWorld(circle.m_p, center)
        circle.m_radius = world.pixelsToWorld(radius)

        return circle
    }
}

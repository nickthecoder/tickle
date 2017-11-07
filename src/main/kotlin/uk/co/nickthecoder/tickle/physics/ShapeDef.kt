package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.Shape

interface ShapeDef {
    fun createShapes(world: TickleWorld): List<Shape>
}

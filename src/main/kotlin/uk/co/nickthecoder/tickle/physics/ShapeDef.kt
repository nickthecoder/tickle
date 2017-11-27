package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.Shape
import uk.co.nickthecoder.tickle.util.Copyable

interface ShapeDef : Copyable<ShapeDef> {
    fun createShapes(world: TickleWorld): List<Shape>
}

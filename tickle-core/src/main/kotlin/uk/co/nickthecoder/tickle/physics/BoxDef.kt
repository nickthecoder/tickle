package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.util.Angle


class BoxDef(
        var width: Double,
        var height: Double,
        var center: Vector2d,
        var angle: Angle)

    : ShapeDef {

    override fun copy(): ShapeDef {
        return BoxDef(width, height, center, angle)
    }

    override fun createShape(world: TickleWorld): Shape {

        val box = PolygonShape()

        box.setAsBox(
                world.pixelsToWorld(width / 2),
                world.pixelsToWorld(height / 2),
                world.pixelsToWorld(center),
                angle.radians.toFloat())

        return box
    }
}

package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.util.Angle


class BoxDef(
        var width: Double,
        var height: Double,
        var center: Vector2d,
        var angle: Angle,
        val roundedEnds: Boolean = false)

    : ShapeDef {

    override fun createShapes(world: TickleWorld): List<Shape> {
        val box = PolygonShape()

        if (roundedEnds) {
            val circle1 = CircleShape()
            val circle2 = CircleShape()

            if (width > height) {
                val radius = (width - height) / 2
                box.setAsBox(
                        world.pixelsToWorld(width / 2 - radius),
                        world.pixelsToWorld(height / 2),
                        world.pixelsToWorld(center),
                        angle.radians.toFloat())

                circle1.m_p.x = world.pixelsToWorld(center.x - Math.sin(angle.radians))
                circle1.m_p.y = world.pixelsToWorld(center.y + Math.cos(angle.radians))
                circle1.m_radius = world.pixelsToWorld(radius)

                circle2.m_p.x = world.pixelsToWorld(center.x + Math.sin(angle.radians))
                circle2.m_p.y = world.pixelsToWorld(center.y - Math.cos(angle.radians))
                circle2.m_radius = world.pixelsToWorld(radius)

            } else {
                val radius = (height - width) / 2
                box.setAsBox(
                        world.pixelsToWorld(width / 2),
                        world.pixelsToWorld(height / 2 - radius),
                        world.pixelsToWorld(center),
                        angle.radians.toFloat())

                circle1.m_p.x = world.pixelsToWorld(center.x + Math.cos(angle.radians))
                circle1.m_p.y = world.pixelsToWorld(center.y - Math.sin(angle.radians))
                circle1.m_radius = world.pixelsToWorld(radius)

                circle2.m_p.x = world.pixelsToWorld(center.x - Math.cos(angle.radians))
                circle2.m_p.y = world.pixelsToWorld(center.y + Math.sin(angle.radians))
                circle2.m_radius = world.pixelsToWorld(radius)
            }

            return listOf(box, circle1, circle2)

        } else {
            box.setAsBox(
                    world.pixelsToWorld(width / 2),
                    world.pixelsToWorld(height / 2),
                    world.pixelsToWorld(center),
                    angle.radians.toFloat())

            return listOf(box)
        }
    }

}

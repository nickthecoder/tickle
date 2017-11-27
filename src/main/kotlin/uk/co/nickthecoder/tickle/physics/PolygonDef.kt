package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.joml.Vector2d

class PolygonDef(points: List<Vector2d> = mutableListOf<Vector2d>()) : ShapeDef {

    val points = points.toMutableList()

    override fun copy(): ShapeDef {
        return PolygonDef(points)
    }

    override fun createShapes(world: TickleWorld): List<Shape> {
        val polygon = PolygonShape()
        polygon.set(Array(points.size) { i -> points.map { world.pixelsToWorld(it) }[i] }, points.size)
        return listOf(polygon)
    }

}

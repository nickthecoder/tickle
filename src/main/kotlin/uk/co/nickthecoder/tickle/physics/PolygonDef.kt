package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Settings
import org.joml.Vector2d

class PolygonDef(points: List<Vector2d> = mutableListOf<Vector2d>()) : ShapeDef {

    val points = points.toMutableList()

    override fun copy(): ShapeDef {
        return PolygonDef(points)
    }

    override fun createShapes(world: TickleWorld): List<Shape> {
        var total = 0.0
        for (i in 0..points.size - 1) {
            val j = (i + 1) % points.size
            total += points[j].x * points[i].y - points[i].x * points[j].y
        }
        if (total > 0.0) {
            val reversed = points.reversed()
            points.clear()
            points.addAll(reversed)
        }

        if (points.size > Settings.maxPolygonVertices) {
            Settings.maxPolygonVertices = points.size
        }

        val polygon = PolygonShape()
        polygon.set(Array(points.size) { i -> points.map { world.pixelsToWorld(it) }[i] }, points.size)
        return listOf(polygon)
    }

}

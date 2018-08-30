/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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

    override fun createShape(world: TickleWorld): Shape {
        var total = 0.0
        for (i in 0..points.size - 1) {
            val j = (i + 1) % points.size
            total += points[j].x * points[i].y - points[i].x * points[j].y
        }
        // Do we need to reverse the order of the polygon points to make them anti-clockwise?
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
        return polygon
    }

}

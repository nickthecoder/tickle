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

import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.Shape
import org.joml.Vector2d

class CircleDef(
        var center: Vector2d,
        var radius: Double = 1.0)

    : ShapeDef {

    override fun copy(): ShapeDef {
        return CircleDef(center, radius)
    }

    override fun createShape(world: TickleWorld): Shape {
        val circle = CircleShape()
        world.pixelsToWorld(circle.m_p, center)
        circle.m_radius = world.pixelsToWorld(radius)

        return circle
    }
}

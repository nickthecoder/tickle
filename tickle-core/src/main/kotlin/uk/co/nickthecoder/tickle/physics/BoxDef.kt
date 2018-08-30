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

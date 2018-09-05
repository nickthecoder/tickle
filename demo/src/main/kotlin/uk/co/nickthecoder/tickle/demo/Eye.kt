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
package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle


class Eye : AbstractRole() {

    lateinit var pupilA: Actor

    val angle = Angle()

    private val mouse = Vector2d()

    override fun activated() {
        pupilA = actor.createChild("pupil")
    }

    override fun tick() {
        actor.stage?.firstView()?.let { view ->
            view.mousePosition(mouse)
            mouse.sub(actor.position)
            angle.of(mouse)
            pupilA.position.set(actor.position).add(angle.vector().mul(5.0, 10.0))
        }
    }
}

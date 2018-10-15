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
package uk.co.nickthecoder.tickle.demo.overlapping

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.demo.TestDirector
import uk.co.nickthecoder.tickle.util.Attribute

class Moving : AbstractRole() {

    @Attribute
    var velocity = Vector2d()

    @Attribute
    var message: String = ""

    @Attribute
    var expected = Vector2d()

    override fun begin() {
        super.begin()
        (Game.instance.director as TestDirector).testCount++
    }

    override fun tick() {
        actor.position.add(velocity)

        val overlapping = (Game.instance.director as OverlappingDirector).overlapping
        actor.stage!!.findRolesByClass(Target::class.java).forEach { other ->

            if (overlapping.overlapping(actor, other.actor)) {
                (Game.instance.director as TestDirector).assertEquals(message, expected, actor.position)
                actor.role = null
            }

        }
    }
}
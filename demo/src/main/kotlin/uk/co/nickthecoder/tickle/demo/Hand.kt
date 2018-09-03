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

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.movement.FollowMouse
import uk.co.nickthecoder.tickle.collision.PixelOverlapping
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.resources.Resources

class Hand : ActionRole() {

    override fun createAction(): Action = FollowMouse(actor.position, actor.stage!!.firstView()!!)

    val overlapKey = Resources.instance.inputs.find("overlap")
    val touchingKey = Resources.instance.inputs.find("touching")

    val pixelOverlap = PixelOverlapping()

    override fun tick() {
        super.tick()

        if (touchingKey?.isPressed() == true) {
            actor.stage?.firstView()?.let { view ->
                println("\nActors at ${actor.position} = ${view.findActorsAt(actor.position).filter { it != actor }}\n\n")
            }
        }

        if (overlapKey?.isPressed() == true) {
            actor.stage?.actors?.forEach { other ->
                if (other !== actor) {
                    if (pixelOverlap.overlapping(other, actor)) {
                        actor.color = Color.red()
                        println("Hand is overlapping $other")
                    } else {
                        actor.color = Color.white()
                    }
                }
            }
        }
    }
}

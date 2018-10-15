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
package uk.co.nickthecoder.tickle.demo.physics

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.physics.TickleMouseJoint
import uk.co.nickthecoder.tickle.stage.StageView

class PhysicsDirector : AbstractDirector() {

    var mouse = Vector2d()

    var mouseJoint: TickleMouseJoint? = null

    lateinit var ground: Actor

    override fun activated() {
        super.activated()
        ground = Game.instance.scene.stages().firstOrNull()!!.findRoleByClass(Ground::class.java)!!.actor
    }

    fun updateMouse() {
        Game.instance.scene.views().firstOrNull()?.mousePosition(mouse)
    }

    override fun onMouseButton(event: MouseEvent) {
        updateMouse()
        if (event.button == 0) {
            if (event.state == ButtonState.PRESSED) {
                Game.instance.scene.views().firstOrNull()?.let { view ->
                    if (view is StageView) {
                        view.stage.findActorAt(mouse)?.let { actor ->
                            mouseJoint = TickleMouseJoint(actor, mouse, 10.0, ground)
                        }

                    }
                }
            } else {
                mouseJoint?.destroy()
                mouseJoint = null
            }
        }
    }

    override fun tick() {
        mouseJoint?.let {
            updateMouse()
            it.target(mouse)
        }

    }
}

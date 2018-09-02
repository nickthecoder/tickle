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
package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Angle

/**
 * Changes a heading by fixed amount when keys are pressed. For a more natural turn, see [GradualTurnInput].
 */
class TurnInput(
        val heading: Angle,
        val turningSpeed: Angle,
        left: String = "left",
        right: String = "right")

    : Action {

    val left = Resources.instance.inputs.find(left) ?: Input.dummyInput
    val right = Resources.instance.inputs.find(right) ?: Input.dummyInput

    override fun act(): Boolean {

        if (left.isPressed()) {
            heading.radians += turningSpeed.radians
        } else if (right.isPressed()) {
            heading.radians -= turningSpeed.radians
        }

        return false
    }

}

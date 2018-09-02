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
import uk.co.nickthecoder.tickle.util.Polar2d

open class AcceleratePolarInput(
        val velocatiry: Polar2d,
        var acceleration: Double,
        var deceleration: Double = -acceleration,
        var autoSlow: Double = 0.0,
        accelerate: String = "up",
        decelerate: String = "down")

    : Action {

    val accelerate = Resources.instance.inputs.find(accelerate) ?: Input.dummyInput
    val decelerate = Resources.instance.inputs.find(decelerate) ?: Input.dummyInput

    override fun act(): Boolean {
        if (accelerate.isPressed()) {
            velocatiry.magnitude += acceleration
        } else if (decelerate.isPressed()) {
            velocatiry.magnitude += deceleration
        } else {
            // Automatically slow down (gradually), when no keys are pressed
            velocatiry.magnitude -= autoSlow
        }
        return false
    }
}

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
package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.action.animation.Turn

/**
 * This is merely an example of ButtonActions.
 * It scales the actor, when the mouse hovers over the button, and shrinks again when it exits.
 * The click action rotates the button through 360 degrees.
 */
open class ExampleButtonEffects(val scale: Double) : ButtonEffects {

    constructor() : this(1.2)

    override fun enter(button: Button): Action? {
        return Scale(button.actor, 0.1, button.actor.scaleXY * 1.2, Eases.easeOut)
    }

    override fun exit(button: Button): Action? {
        return Scale(button.actor, 0.1, button.actor.scaleXY / 1.2, Eases.easeIn)
    }

    override fun clicked(button: Button): Action? {
        return Turn(button.actor.direction, 0.5, button.actor.direction + Angle.degrees(360.0), Eases.easeInOut)
    }

}

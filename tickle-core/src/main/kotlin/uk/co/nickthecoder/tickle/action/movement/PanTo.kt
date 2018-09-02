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
package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp
import uk.co.nickthecoder.tickle.stage.StageView

open class PanTo(
        val view: StageView,
        val finalPosition: Vector2d,
        seconds: Double,
        ease: Ease = LinearEase())

    : AnimationAction(seconds, ease) {

    var initialPosition = Vector2d()

    override fun storeInitialValue() {
        initialPosition.set(view.centerX, view.centerY)
    }

    override fun update(t: Double) {
        view.centerX = lerp(initialPosition.x, finalPosition.x, t)
        view.centerY = lerp(initialPosition.y, finalPosition.y, t)
    }

}

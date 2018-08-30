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
package uk.co.nickthecoder.tickle.action

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.lerp
import uk.co.nickthecoder.tickle.stage.StageView

class CenterViewBetween(
        val stageView: StageView,
        val positionA: Vector2d,
        val positionB: Vector2d,
        seconds: Double = 0.5,
        ease: Ease = Eases.easeInOut)

    : AnimationAction(seconds, ease) {

    override fun storeInitialValue() {
    }

    override fun update(t: Double) {
        stageView.centerX = lerp(positionA.x, positionB.x, t)
        stageView.centerY = lerp(positionA.y, positionB.y, t)
    }
}
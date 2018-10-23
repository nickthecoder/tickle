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
package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.graphics.Color

class Fade(
        val color: Color,
        seconds: Double,
        val finalAlpha: Float,
        ease: Ease)

    : AnimationAction(seconds, ease) {

    constructor(color: Color, seconds: Double, finalAlpha: Float) : this(color, seconds, finalAlpha, LinearEase.instance)

    private var initialAlpha = 0f

    override fun storeInitialValue() {
        initialAlpha = color.alpha
    }

    override fun update(t: Double) {
        color.alpha = lerp(initialAlpha, finalAlpha, t.toFloat())
    }
}

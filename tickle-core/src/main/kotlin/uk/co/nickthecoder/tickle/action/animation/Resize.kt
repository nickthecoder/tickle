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

import uk.co.nickthecoder.tickle.Actor

class Resize(
        val actor: Actor,
        seconds: Double,
        val finalWidth: Double = actor.appearance.width(),
        val finalHeight: Double = actor.appearance.height(),
        ease: Ease)

    : AnimationAction(seconds, ease) {

    constructor(actor: Actor, seconds: Double, finalWidth: Double, finalHeight: Double) : this(actor, seconds, finalWidth, finalHeight, LinearEase.instance)

    private var initialWidth = 0.0
    private var initialHeight = 0.0

    override fun storeInitialValue() {
        initialWidth = actor.appearance.width()
        initialHeight = actor.appearance.height()
    }

    override fun update(t: Double) {
        actor.resize(lerp(initialWidth, finalWidth, t), lerp(initialHeight, finalHeight, t))
    }

}

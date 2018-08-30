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
package uk.co.nickthecoder.tickle.loop

import uk.co.nickthecoder.tickle.Game

abstract class AbstractGameLoop(val game: Game) : GameLoop {

    private var startNanos = System.nanoTime()

    override var tickCount = 0L

    override fun resetStats() {
        startNanos = System.nanoTime()
        tickCount = 0
    }

    override fun actualFPS(): Double {
        return (tickCount.toDouble() * 1_000_000_000 / (System.nanoTime() - startNanos))
    }
}

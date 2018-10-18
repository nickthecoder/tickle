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

/**
 * Responsible for coordinating the "tick" calls to the Producer, the Director, all StageViews, all Stages
 * and all TickleWorlds (if physics is enabled).
 * All Role's tick methods are called by Stage's tick method, not from GameLoop directly.
 *
 * The default implementation is [FullSpeedGameLoop]. However, you may create your own implementation
 * if you game has special needs.
 *
 * For example, you could ensure that ticks have a constant time step (so that your game is predictably
 * even running, regardless of load). To do this, call the tick methods multiple times when under light load, and
 * skipping some/all tick calls when the frame rate drops too low.
 */
interface GameLoop {
    val tickCount: Long
    fun sceneStarted()
    fun tick()
    fun resetStats()
    fun actualFPS(): Double
}

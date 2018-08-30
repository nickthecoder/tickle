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

import uk.co.nickthecoder.tickle.Game

class Delay(val seconds: Double) : Action {

    var endSeconds: Double = 0.0

    override fun begin(): Boolean {
        endSeconds = Game.instance.seconds + seconds
        return seconds <= 0.0
    }

    override fun act(): Boolean {
        return Game.instance.seconds >= endSeconds
    }
}

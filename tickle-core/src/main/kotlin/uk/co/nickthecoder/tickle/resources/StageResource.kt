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
package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.Costume

/**
 * Details of all the Actors' initial state.
 * Used when loading and editing a Scene. Not used during actual game play.
 */
class StageResource() {

    val actorResources = mutableListOf<ActorResource>()

    fun dependsOn(costume: Costume): Boolean {
        for (ar in actorResources) {
            if (ar.costume() == costume) {
                return true
            }
        }
        return false
    }
}

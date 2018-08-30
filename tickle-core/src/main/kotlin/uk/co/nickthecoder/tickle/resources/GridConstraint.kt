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

import uk.co.nickthecoder.tickle.util.Attribute

/**
 * Snaps actors to a grid
 */
class GridConstraint : NoStageConstraint() {

    @Attribute
    var xSpacing: Double = 40.0

    @Attribute
    var ySpacing: Double = 40.0

    override fun addActorResource(actorResource: ActorResource) {
        adjust(actorResource)
    }

    override fun snapActor(actorResource: ActorResource, isNew: Boolean): Boolean {
        adjust(actorResource)
        return true
    }

    fun adjust(actorResource: ActorResource) {
        actorResource.x = Math.round((actorResource.draggedX / xSpacing)) * xSpacing
        actorResource.y = Math.round((actorResource.draggedY / ySpacing)) * ySpacing
    }

}

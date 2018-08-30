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

import java.util.jar.Attributes

interface StageConstraint {

    val attributes: Attributes

    fun forStage(stageName: String, stageResource: StageResource)

    fun addActorResource(actorResource: ActorResource)

    fun removeActorResource(actorResource: ActorResource)

    /**
     * Return false iff further snapping can be done.
     */
    fun snapActor(actorResource: ActorResource, isNew: Boolean ): Boolean

}

open class NoStageConstraint : StageConstraint {

    override val attributes = Attributes()

    override fun forStage(stageName: String, stageResource: StageResource) {}

    override fun addActorResource(actorResource: ActorResource) {}

    override fun removeActorResource(actorResource: ActorResource) {}

    override fun snapActor(actorResource: ActorResource, isNew: Boolean): Boolean {
        actorResource.x = actorResource.draggedX
        actorResource.y = actorResource.draggedY
        return false
    }
}
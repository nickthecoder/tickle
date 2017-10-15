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

    override fun addActorResource(actorResource: ActorResource): Boolean {
        adjust(actorResource)
        return true
    }

    override fun moveActorResource(actorResource: ActorResource, isNew: Boolean) {
        adjust(actorResource)
    }

    fun adjust(actorResource: ActorResource) {
        actorResource.x = Math.round((actorResource.draggedX / xSpacing)) * xSpacing
        actorResource.y = Math.round((actorResource.draggedY / ySpacing)) * ySpacing
    }

}

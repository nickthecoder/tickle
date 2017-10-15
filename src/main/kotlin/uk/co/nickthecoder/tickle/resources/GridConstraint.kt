package uk.co.nickthecoder.tickle.resources

/**
 * Snaps actors to a grid
 */
class GridConstraint : StageConstraint {

    var xSpacing: Double = 40.0
    var ySpacing: Double = 40.0

    override fun forStage(stageName: String, stageResource: StageResource) {
    }

    override fun addActorResource(actorResource: ActorResource): Boolean {
        adjust(actorResource)
        return true
    }

    override fun removeActorResource(actorResource: ActorResource) {
    }

    override fun moveActorResource(actorResource: ActorResource, isNew: Boolean) {
        adjust(actorResource)
    }

    fun adjust(actorResource: ActorResource) {
        actorResource.x = Math.round((actorResource.draggedX / xSpacing)) * xSpacing
        actorResource.y = Math.round((actorResource.draggedY / ySpacing)) * ySpacing
    }

}

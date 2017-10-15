package uk.co.nickthecoder.tickle.resources

interface StageConstraint {

    fun forStage(stageName: String, stageResource: StageResource)

    fun addActorResource(actorResource: ActorResource): Boolean

    fun removeActorResource(actorResource: ActorResource)

    fun moveActorResource(actorResource: ActorResource, isNew: Boolean)

}

class NoStageConstraint : StageConstraint {

    override fun forStage(stageName: String, stageResource: StageResource) {}

    override fun addActorResource(actorResource: ActorResource) = true

    override fun removeActorResource(actorResource: ActorResource) {}

    override fun moveActorResource(actorResource: ActorResource, isNew: Boolean) {
        actorResource.x = actorResource.draggedX
        actorResource.y = actorResource.draggedY
    }
}
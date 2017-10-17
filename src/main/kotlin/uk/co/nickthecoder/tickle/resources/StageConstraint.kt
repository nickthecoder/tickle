package uk.co.nickthecoder.tickle.resources

import java.util.jar.Attributes

interface StageConstraint {

    val attributes : Attributes

    fun forStage(stageName: String, stageResource: StageResource)

    fun addActorResource(actorResource: ActorResource): Boolean

    fun removeActorResource(actorResource: ActorResource)

    fun moveActorResource(actorResource: ActorResource, isNew: Boolean)

}

open class NoStageConstraint : StageConstraint {

    override val attributes = Attributes()

    override fun forStage(stageName: String, stageResource: StageResource) {}

    override fun addActorResource(actorResource: ActorResource) = true

    override fun removeActorResource(actorResource: ActorResource) {}

    override fun moveActorResource(actorResource: ActorResource, isNew: Boolean) {
        actorResource.x = actorResource.draggedX
        actorResource.y = actorResource.draggedY
    }
}
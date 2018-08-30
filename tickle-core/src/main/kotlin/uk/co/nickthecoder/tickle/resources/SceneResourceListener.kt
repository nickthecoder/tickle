package uk.co.nickthecoder.tickle.resources


interface SceneResourceListener {

    fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType)

}

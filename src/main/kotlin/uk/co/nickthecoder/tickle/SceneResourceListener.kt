package uk.co.nickthecoder.tickle


interface SceneResourceListener {

    fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType)

}

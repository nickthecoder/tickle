package uk.co.nickthecoder.tickle.editor.resources


interface SceneResourceListener {

    fun actorModified(sceneResource: DesignSceneResource, actorResource: DesignActorResource, type: ModificationType)

}

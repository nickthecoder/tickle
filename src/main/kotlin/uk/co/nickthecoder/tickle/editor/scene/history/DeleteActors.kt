package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.scene.StageLayer
import uk.co.nickthecoder.tickle.resources.ActorResource

class DeleteActors : Change {

    private val list = mutableListOf<Pair<ActorResource, StageLayer>>()

    constructor(actorResources: Iterable<ActorResource>) {
        actorResources.forEach { actorResource ->
            val layer = actorResource.layer
            if (layer != null) {
                list.add(Pair(actorResource, layer))
            }
        }
    }

    override fun redo(sceneEditor: SceneEditor) {
        list.forEach { item ->
            sceneEditor.delete(item.first)
        }
    }

    override fun undo(sceneEditor: SceneEditor) {
        list.forEach { item ->
            sceneEditor.addActor(item.first, item.second)
        }
    }
}
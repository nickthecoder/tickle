package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.scene.StageLayer

class DeleteActors : Change {

    private val list = mutableListOf<Pair<DesignActorResource, StageLayer>>()

    constructor(actorResources: Iterable<DesignActorResource>) {
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
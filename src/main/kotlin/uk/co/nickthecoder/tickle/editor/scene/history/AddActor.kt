package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource

class AddActor(private val actorResource: ActorResource) : Change {

    override fun redo(sceneEditor: SceneEditor) {
        sceneEditor.addActor(actorResource)
    }

    override fun undo(sceneEditor: SceneEditor) {
        sceneEditor.delete(actorResource)
    }

}

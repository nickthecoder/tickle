package uk.co.nickthecoder.tickle.editor.resources

import uk.co.nickthecoder.tickle.editor.scene.SnapRotation
import uk.co.nickthecoder.tickle.editor.scene.SnapToGrid
import uk.co.nickthecoder.tickle.editor.scene.SnapToGuides
import uk.co.nickthecoder.tickle.editor.scene.SnapToOthers
import uk.co.nickthecoder.tickle.resources.SceneResource

class DesignSceneResource : SceneResource() {

    val snapToGrid = SnapToGrid()

    val snapToGuides = SnapToGuides()

    val snapToOthers = SnapToOthers()

    val snapRotation = SnapRotation()

    val listeners = mutableSetOf<SceneResourceListener>()

    fun fireChange(actorResource: DesignActorResource, type: ModificationType) {
        listeners.toList().forEach {
            it.actorModified(this, actorResource, type)
        }
    }
}

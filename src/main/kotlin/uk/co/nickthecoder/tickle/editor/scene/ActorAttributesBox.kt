package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.resources.ActorResource

class ActorAttributesBox(val sceneEditor: SceneEditor)
    : SelectionListener {

    val stack = StackPane()

    var actorAttributesForm: ActorAttributesForm? = null

    var actorResource: ActorResource?
        get() = actorAttributesForm?.actorResource
        set(v) {
            if (actorAttributesForm?.actorResource != v) {
                actorAttributesForm?.cleanUp()
                stack.children.clear()
                if (v != null) {
                    actorAttributesForm = ActorAttributesForm(v, sceneEditor.sceneResource)
                    stack.children.add(actorAttributesForm!!.build())
                }
            }
        }

    init {
        sceneEditor.selection.listeners.add(this)
    }

    fun build(): Node {
        return stack
    }

    override fun selectionChanged() {
        actorResource = sceneEditor.selection.latest()
    }

}


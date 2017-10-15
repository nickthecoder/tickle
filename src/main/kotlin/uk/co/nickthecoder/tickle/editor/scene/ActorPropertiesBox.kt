package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.resources.ActorResource

class ActorPropertiesBox(val sceneEditor: SceneEditor)
    : SelectionListener {

    val stack = StackPane()

    var actorProperties: ActorProperties? = null

    var actorResource: ActorResource? = null
        set(v) {
            if (field != v) {
                actorProperties?.cleanUp()
                stack.children.clear()
                if (v != null) {
                    actorProperties = ActorProperties(v, sceneEditor.sceneResource)
                    stack.children.add(actorProperties!!.build())
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


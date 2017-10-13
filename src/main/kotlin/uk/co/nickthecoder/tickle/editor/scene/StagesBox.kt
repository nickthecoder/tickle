package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.EditorAction

class StagesBox(val sceneResource: SceneResource) {

    val tree = TreeView<String>()

    val root = TreeItem<String>("Stages")

    fun build(): Node {

        tree.root = root
        tree.isShowRoot = false

        sceneResource.stageResources.forEach { stageName, stage ->
            root.children.add(StageItem(stageName, stage))
        }

        return tree

    }

    inner class StageItem(val stageName: String, val stage: StageResource)

        : TreeItem<String>(stageName), SceneResourceListener {

        init {
            sceneResource.listeners.add(this)
            stage.actorResources.forEach { actor ->
                children.add(ActorItem(actor))
            }
        }

        override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
            if (type == ModificationType.NEW) {
                children.add(ActorItem(actorResource))
            }
        }

        override fun isLeaf() = false
    }

    inner class ActorItem(val actor: ActorResource)

        : TreeItem<String>(actor.costumeName), SceneResourceListener {

        init {
            sceneResource.listeners.add(this)
            graphic = ImageView(EditorAction.imageResource("actor.png"))
            updateLabel()
        }

        override fun actorModified(sceneResource: SceneResource, actor: ActorResource, type: ModificationType) {
            if (actor === this.actor) {
                when (type) {
                    ModificationType.DELETE -> {
                        parent?.children?.remove(this)
                        sceneResource.listeners.remove(this)
                    }
                    ModificationType.CHANGE -> updateLabel()
                    ModificationType.NEW -> Unit
                }
            }
        }

        fun updateLabel() {
            value = "${actor.costumeName} ${actor.x.toInt()},${actor.y.toInt()}"
        }

        override fun isLeaf() = true
    }
}
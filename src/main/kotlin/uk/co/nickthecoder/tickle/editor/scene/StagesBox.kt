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
            stage.actorResources.forEach { actor ->
                children.add(ActorItem(actor))
            }
        }

        override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
        }

        override fun isLeaf() = false
    }

    inner class ActorItem(val actor: ActorResource)

        : TreeItem<String>(actor.costumeName), SceneResourceListener {

        init {
            graphic = ImageView(EditorAction.imageResource("actor.png"))
        }

        override fun actorModified(sceneResource: SceneResource, actor: ActorResource, type: ModificationType) {
        }

        override fun isLeaf() = true
    }
}
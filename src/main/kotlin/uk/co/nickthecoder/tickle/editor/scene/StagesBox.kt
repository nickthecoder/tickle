package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.SceneStage
import uk.co.nickthecoder.tickle.editor.EditorAction

class StagesBox(val sceneResource: SceneResource) {

    val tree = TreeView<String>()

    val root = TreeItem<String>("Stages")

    fun build(): Node {

        tree.root = root
        tree.isShowRoot = false

        sceneResource.sceneStages.forEach { stageName, stage ->
            root.children.add(StageItem(stageName, stage))
        }

        return tree

    }

    inner class StageItem(val stageName: String, val stage: SceneStage)

        : TreeItem<String>(stageName), SceneListerner {

        init {
            stage.sceneActors.forEach { actor ->
                children.add(ActorItem(actor))
            }
        }

        override fun sceneChanged(sceneResource: SceneResource) {

        }

        override fun isLeaf() = false
    }

    inner class ActorItem(val actor: SceneActor)

        : TreeItem<String>(actor.costumeName), SceneListerner {

        init {
            graphic = ImageView(EditorAction.imageResource("actor.png"))
        }

        override fun sceneChanged(sceneResource: SceneResource) {

        }

        override fun isLeaf() = true
    }
}
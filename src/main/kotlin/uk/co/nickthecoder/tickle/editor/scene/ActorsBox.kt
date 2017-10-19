package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType
import uk.co.nickthecoder.tickle.resources.SceneResource
import uk.co.nickthecoder.tickle.resources.SceneResourceListener

class ActorsBox(val sceneEditor: SceneEditor) {

    val sceneResource: SceneResource = sceneEditor.sceneResource

    val tree = TreeView<String>()

    val root = TreeItem<String>("Stages")

    init {
        tree.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue is ActorItem) {
                sceneEditor.selection.clearAndSelect(newValue.actorResource)
            }
        }
    }

    fun build(): Node {

        tree.root = root
        tree.isShowRoot = false

        sceneEditor.layers.stageLayers().forEach { layer ->
            root.children.add(StageItem(layer))
        }

        return tree

    }

    inner class StageItem(val layer: StageLayer)

        : TreeItem<String>(layer.stageName), SceneResourceListener {

        val groups = mutableMapOf<String, List<ActorResource>>()

        val stage = sceneResource.stageResources[layer.stageName]!!

        init {
            sceneResource.listeners.add(this)
            groups.putAll(stage.actorResources.groupBy { it.costumeName })

            groups.forEach { costumeName, actorList ->
                children.add(CostumeGroupItem(layer, costumeName, actorList))
            }
            isExpanded = true
        }


        override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
            // Add a new CostumeGroupItem if this is the first actor with this costume added to the scene.
            if (type == ModificationType.NEW && actorResource.layer === layer) {
                if (children.firstOrNull { it.value == actorResource.costumeName } == null) {
                    children.add(CostumeGroupItem(layer, actorResource.costumeName, listOf(actorResource)))
                }
            }
        }

        override fun isLeaf() = false
    }

    inner class CostumeGroupItem(val layer: Layer, val costumeName: String, actors: List<ActorResource>)

        : TreeItem<String>(costumeName), SceneResourceListener {

        val actorList = actors.toMutableList()

        init {
            sceneResource.listeners.add(this)
            actors.forEach { actor ->
                children.add(ActorItem(actor))
            }
        }

        override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
            if (type == ModificationType.NEW && actorResource.layer === layer && actorResource.costumeName == value) {
                children.add(ActorItem(actorResource))
            }
        }

        override fun isLeaf() = false
    }

    inner class ActorItem(val actorResource: ActorResource)

        : TreeItem<String>(actorResource.costumeName), SceneResourceListener {

        init {
            sceneResource.listeners.add(this)
            graphic = ImageView(EditorAction.imageResource("actor.png"))
            updateLabel()
        }

        override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
            if (actorResource === this.actorResource) {
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
            value = "${actorResource.costumeName} ${actorResource.x.toInt()},${actorResource.y.toInt()}"
        }

        override fun isLeaf() = true
    }
}
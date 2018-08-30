package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.*
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.SceneResource

class ActorsBox(val sceneEditor: SceneEditor) {

    val sceneResource: DesignSceneResource = sceneEditor.sceneResource

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

        tree.addEventHandler(MouseEvent.MOUSE_PRESSED) { onMouse(it) }
        tree.addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouse(it) }
        return tree

    }

    fun onMouse(event: MouseEvent) {
        if (event.isPopupTrigger) {
            tree.selectionModel.selectedItem?.let {
                val item = it as MyTreeItem?
                item?.createContextMenu()?.show(MainWindow.instance.scene.window, event.screenX, event.screenY)
                event.consume()
            }
        }
    }


    private abstract class MyTreeItem(label: String) : TreeItem<String>(label) {
        open fun createContextMenu(): ContextMenu? = null
    }

    private inner class StageItem(val layer: StageLayer)

        : MyTreeItem(layer.stageName), SceneResourceListener {

        val groups = mutableMapOf<String, List<DesignActorResource>>()

        val stage = sceneResource.stageResources[layer.stageName]!!

        init {
            sceneResource.listeners.add(this)
            stage.actorResources.groupBy { it.costumeName }.forEach { name, list ->
                groups.put(name, list.map { it as DesignActorResource })
            }

            groups.forEach { costumeName, actorList ->
                children.add(CostumeGroupItem(layer, costumeName, actorList))
            }
            isExpanded = true
        }


        override fun actorModified(sceneResource: DesignSceneResource, actorResource: DesignActorResource, type: ModificationType) {
            // Add a new CostumeGroupItem if this is the first actor with this costume added to the scene.
            if (type == ModificationType.NEW && actorResource.layer === layer) {
                if (children.firstOrNull { it.value == actorResource.costumeName } == null) {
                    children.add(CostumeGroupItem(layer, actorResource.costumeName, listOf(actorResource)))
                }
            }
        }

        override fun isLeaf() = false
    }

    private inner class CostumeGroupItem(val layer: Layer, val costumeName: String, actors: List<DesignActorResource>)

        : MyTreeItem(costumeName), SceneResourceListener {

        init {
            sceneResource.listeners.add(this)
            actors.forEach { actor ->
                children.add(ActorItem(actor))
            }
        }

        override fun actorModified(sceneResource: DesignSceneResource, actorResource: DesignActorResource, type: ModificationType) {
            if (type == ModificationType.NEW && actorResource.layer === layer && actorResource.costumeName == value) {
                children.add(ActorItem(actorResource))
            }
        }

        override fun createContextMenu(): ContextMenu? {
            val menu = ContextMenu()
            val delete = MenuItem("Delete (${children.count()} actors)")
            delete.onAction = EventHandler {
                onDelete()
            }

            menu.items.addAll(delete)

            return menu
        }

        fun onDelete() {
            children.filterIsInstance<ActorItem>().forEach {
                it.onDelete()
            }
            parent?.children?.remove(this)
        }

        override fun isLeaf() = false
    }

    private inner class ActorItem(val actorResource: DesignActorResource)

        : MyTreeItem(actorResource.costumeName), SceneResourceListener {

        init {
            sceneResource.listeners.add(this)
            graphic = ImageView(EditorAction.imageResource("actor.png"))
            updateLabel()
        }

        override fun actorModified(sceneResource: DesignSceneResource, actorResource: DesignActorResource, type: ModificationType) {
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

        override fun createContextMenu(): ContextMenu? {
            val menu = ContextMenu()
            val delete = MenuItem("Delete")
            delete.onAction = EventHandler {
                onDelete()
            }

            menu.items.addAll(delete)

            return menu
        }

        fun onDelete() {
            actorResource.layer?.stageResource?.actorResources?.remove(actorResource)
            sceneResource.fireChange(actorResource, ModificationType.DELETE)
        }

        fun updateLabel() {
            value = "${actorResource.costumeName} ${actorResource.x.toInt()},${actorResource.y.toInt()}"
        }

        override fun isLeaf() = true
    }
}
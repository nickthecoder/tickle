package uk.co.nickthecoder.tickle.editor

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

class ResourcesTree()

    : TreeView<String>() {

    val resources = Resources.instance

    init {
        isEditable = false
        root = RootItem()
        root.children
        // Prevent Double Click expanding/contracting the item (as this is used to show the contents of the directory).
        addEventFilter(MouseEvent.MOUSE_PRESSED) { if (it.clickCount == 2) it.consume() }
        addEventFilter(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }

        root.isExpanded = true
    }

    fun onMouseClicked(event: MouseEvent) {
        if (event.clickCount == 2) {
            editItem()
            event.consume()
        }
    }

    fun onKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            editItem()
            event.consume()
        }
    }

    fun editItem() {
        val item = selectionModel.selectedItem
        if (item == null) return

        if (item is DataItem) {

            val data = item.data()

            MainWindow.instance?.openTab(item.value, data)

        } else {
            item.isExpanded = !item.isExpanded
        }
    }

    abstract inner class ResourceItem(val label: String = "") : TreeItem<String>(label) {
        open fun data(): Any? = null

        override fun isLeaf() = true

        open fun removed() {}

        fun remove(child: ResourceItem) {
            children.remove(child)
            child.removed()
            updateLabel()
        }

        fun clear() {
            children.forEach { child ->
                (child as ResourceItem).removed()
            }
            children.clear()
        }

        fun updateLabel() {
            value = toString()
        }

        override fun toString() = label
    }


    inner class RootItem() : ResourceItem("Resources") {

        init {
            children.addAll(
                    GameInfoItem(),
                    TexturesItem(),
                    PosesItem(),
                    CostumesItem(),
                    InputsItem(),
                    LayoutsItem(),
                    ScenesDirectoryItem("Scenes", resources.sceneDirectory.absoluteFile))
        }

        override fun isLeaf() = false
    }

    inner class GameInfoItem() : ResourceItem("Game Info") {

        init {
            resources.poses().forEach { name, pose ->
                children.add(DataItem(name, pose))
            }
        }

        override fun data(): GameInfo = resources.gameInfo
    }

    open inner class DataItem(val name: String, val data: Any) : ResourceItem(name), ResourcesListener {

        init {
            Resources.instance.listeners.add(this)
        }

        override fun data() = data

        override fun resourceRenamed(resource: Any, oldName: String, newName: String) {
            if (resource === data) {
                updateLabel()
            }
        }

        override fun resourceRemoved(resource: Any, name: String) {
            if (resource === data) {
                parent?.let {
                    (it as ResourceItem).remove(this)
                }
            }
        }

        override fun removed() {
            Resources.instance.listeners.remove(this)
        }
    }

    abstract inner class TopLevelItem(label: String = "") : ResourceItem(label), ResourcesListener {
        init {
            Resources.instance.listeners.add(this)
        }

        override fun isLeaf() = false

        override fun removed() {
            Resources.instance.listeners.remove(this)
        }
    }

    inner class TexturesItem() : TopLevelItem() {

        init {
            resources.textures().map { it }.sortedBy { it.key }.forEach { (name, texture) ->
                children.add(TextureItem(name, texture))
            }
            updateLabel()
            isExpanded = true
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Texture) {
                children.add(TextureItem(name, resource))
                updateLabel()
            }
        }

        override fun toString() = "Textures (${children.size})"
    }

    inner class TextureItem(name: String, val texture: Texture)

        : DataItem(name, texture) {

        init {
            resources.poses().filter { it.value.texture === texture }.map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(DataItem(name, pose))
            }
        }

        override fun resourceRemoved(resource: Any, name: String) {
            super.resourceRemoved(resource, name)
            if (resource is Pose && resource.texture === texture) {
                children.filterIsInstance<DataItem>().firstOrNull { it.data === resource }?.let {
                    remove(it)
                }
            }
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Pose) {
                if (resource.texture === texture) {
                    children.add(DataItem(name, resource))
                    updateLabel()
                }
            }
        }

        override fun isLeaf() = false
    }

    inner class PosesItem() : TopLevelItem() {

        init {
            resources.poses().map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(DataItem(name, pose))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Pose) {
                children.add(DataItem(name, resource))
                updateLabel()
            }
        }

        override fun toString() = "All Poses (${children.size})"
    }


    inner class CostumesItem() : TopLevelItem() {

        init {
            resources.costumes().map { it }.sortedBy { it.key }.forEach { (name, costume) ->
                children.add(DataItem(name, costume))
            }
            value = toString()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Costume) {
                children.add(DataItem(name, resource))
                updateLabel()
            }
        }

        override fun toString() = "Costumes (${children.size})"

    }

    inner class LayoutsItem() : TopLevelItem() {

        init {
            resources.layouts().map { it }.sortedBy { it.key }.forEach { (name, layout) ->
                children.add(DataItem(name, layout))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Layout) {
                children.add(DataItem(name, resource))
                updateLabel()
            }
        }

        override fun toString() = "Layouts (${children.size})"

    }


    inner class InputsItem() : TopLevelItem() {

        init {
            resources.inputs().map { it }.sortedBy { it.key }.forEach { (name, input) ->
                children.add(DataItem(name, input))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is CompoundInput) {
                children.add(DataItem(name, resource))
                updateLabel()
            }
        }

        override fun toString() = "Inputs (${children.size})"

    }

    inner class ScenesDirectoryItem(label: String, val directory: File)
        : TopLevelItem(label) {

        init {
            val directoryLister = FileLister(onlyFiles = false)
            directoryLister.listFiles(directory).forEach { file ->
                children.add(ScenesDirectoryItem(file.name, file))
            }
            val sceneLister = FileLister(extensions = listOf("scene"))
            sceneLister.listFiles(directory).forEach { file ->
                children.add(DataItem(file.nameWithoutExtension, SceneStub(file)))
            }
            isExpanded = true
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is File && resource.parentFile == directory) {
                if (resource.isDirectory) {
                    children.add(ScenesDirectoryItem(resource.name, resource))
                } else if (resource.extension == "scene") {
                    children.add(DataItem(resource.nameWithoutExtension, SceneStub(resource)))
                }
            }
        }

        override fun isLeaf() = false

    }

}

class SceneStub(val file: File)

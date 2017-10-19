package uk.co.nickthecoder.tickle.editor

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Layout
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.ResourcesListener
import java.io.File

class ResourcesTree()

    : TreeView<String>() {

    val resources = Resources.instance

    init {
        isEditable = false
        root = RootItem()
        isShowRoot = false
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

            MainWindow.instance.openTab(item.value, data)

        } else {
            item.isExpanded = !item.isExpanded
        }
    }

    abstract inner class ResourceItem(label: String = "") : TreeItem<String>(label) {
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

        override fun toString() = value
    }


    inner class RootItem : ResourceItem(resources.file.nameWithoutExtension) {

        init {
            children.addAll(
                    GameInfoItem(),
                    TexturesItem(),
                    PosesItem(),
                    FontResourcesItem(),
                    CostumesItem(),
                    InputsItem(),
                    LayoutsItem(),
                    ScenesDirectoryItem("Scenes", resources.sceneDirectory.absoluteFile))
        }

        override fun isLeaf() = false
    }

    inner class GameInfoItem() : DataItem("Game Info", resources.gameInfo, "gameInfo.png") {

        init {
            resources.poses.items().forEach { name, pose ->
                children.add(DataItem(name, pose))
            }
        }

        override fun data(): GameInfo = resources.gameInfo
    }

    open inner class DataItem(var name: String, val data: Any, val graphicName: String = "unknown.png")

        : ResourceItem(name), ResourcesListener {

        init {
            resources.listeners.add(this)
            graphic = ImageView(EditorAction.imageResource(graphicName))
        }

        override fun data() = data

        override fun resourceRenamed(resource: Any, oldName: String, newName: String) {
            if (resource === data) {
                if (name == oldName) {
                    name = newName
                    value = name
                }
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
            resources.listeners.remove(this)
        }
    }

    abstract inner class TopLevelItem(label: String = "", graphicName: String = "folder2.png") : ResourceItem(label), ResourcesListener {
        init {
            resources.listeners.add(this)
            graphic = ImageView(EditorAction.imageResource(graphicName))
        }

        override fun isLeaf() = false

        override fun removed() {
            resources.listeners.remove(this)
        }

    }

    inner class TexturesItem() : TopLevelItem() {

        init {
            resources.textures.items().map { it }.sortedBy { it.key }.forEach { (name, texture) ->
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

        : DataItem(name, texture, "texture.png") {

        init {
            resources.poses.items().filter { it.value.texture === texture }.map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(DataItem(name, pose, "pose.png"))
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
                    children.add(DataItem(name, resource, "pose.png"))
                }
            }
        }

        override fun isLeaf() = false
    }

    inner class PosesItem() : TopLevelItem() {

        init {
            resources.poses.items().map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(DataItem(name, pose, "pose.png"))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Pose) {
                children.add(DataItem(name, resource, "pose.png"))
                updateLabel()
            }
        }

        override fun toString() = "All Poses (${children.size})"
    }


    inner class FontResourcesItem() : TopLevelItem() {

        init {
            resources.fontResources.items().map { it }.sortedBy { it.key }.forEach { (name, fontResource) ->
                children.add(DataItem(name, fontResource, "font.png"))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is FontResource) {
                children.add(DataItem(name, resource, "font.png"))
                updateLabel()
            }
        }

        override fun toString() = "Fonts (${children.size})"
    }


    inner class CostumesItem() : TopLevelItem() {

        init {
            println("Costume group : ${resources.costumeGroups.items()}")
            resources.costumeGroups.items().map { it }.sortedBy { it.key }.forEach { (groupName, costumeGroup) ->
                children.add(CostumeGroupItem(groupName, costumeGroup))
            }
            resources.costumes.items().map { it }.sortedBy { it.key }.forEach { (costumeName, costume) ->
                if (resources.findCostumeGroup(costumeName) == null) {
                    children.add(CostumeItem(costumeName, costume, null))
                }
            }
            value = toString()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is CostumeGroup) {
                children.add(CostumeGroupItem(name, resource))
                updateLabel()
            }
            if (resource is Costume) {
                if (resources.findCostumeGroup(name) == null) {
                    children.add(CostumeItem(name, resource, null))
                    updateLabel()
                }
            }
        }

        /**
         * If a costume was removed from a CostumeGroup, then we need to ADD it to our children.
         */
        override fun resourceRemoved(resource: Any, name: String) {
            if (resource is Costume) {
                if (resources.costumes.findName(resource) != null && resources.findCostumeGroup(name) == null) {
                    children.add(CostumeItem(name, resource, null))
                }
            }
        }

        override fun toString() = "Costumes (${children.size})"

    }

    inner class CostumeGroupItem(val name: String, val costumeGroup: CostumeGroup) : TopLevelItem(name) {

        init {
            costumeGroup.items().map { it }.sortedBy { it.key }.forEach { (costumeName, costume) ->
                children.add(CostumeItem(costumeName, costume, costumeGroup))
            }
            value = toString()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Costume && costumeGroup.findName(resource) != null) {
                children.add(CostumeItem(name, resource, costumeGroup))
                updateLabel()
            }
        }

        override fun toString() = "$name (${children.size})"

    }

    inner class CostumeItem(name: String, val costume: Costume, val costumeGroup: CostumeGroup?)

        : DataItem(name, costume, "costume.png") {

        override fun resourceRemoved(resource: Any, name: String) {
            if (resource === costume) {
                if (!exists(resource)) {
                    parent?.let { (it as ResourceItem).remove(this) }
                }
            }
        }

        /**
         * If a costume was ADDED to a CostumeGroup, we need to REMOVE it, if this is in the non-group
         */
        override fun resourceAdded(resource: Any, name: String) {
            if (costumeGroup == null && resource === costume) {
                println("Resource Added : $name = $resource")
                if (resources.findCostumeGroup(name) != null) {
                    println("Was null")
                    parent?.let {
                        println("Removing")
                        (it as ResourceItem).remove(this)
                    }
                }
            }
        }

        fun exists(costume: Costume): Boolean {
            if (costumeGroup == null) {
                val costumeName = resources.costumes.findName(costume)
                return costumeName != null && resources.findCostumeGroup(costumeName) == null
            } else {
                return costumeGroup.findName(costume) != null
            }
        }
    }

    inner class LayoutsItem() : TopLevelItem() {

        init {
            resources.layouts.items().map { it }.sortedBy { it.key }.forEach { (name, layout) ->
                children.add(DataItem(name, layout, "layout.png"))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Layout) {
                children.add(DataItem(name, resource, "layout.png"))
                updateLabel()
            }
        }

        override fun toString() = "Layouts (${children.size})"

    }

    inner class InputsItem() : TopLevelItem() {

        init {
            resources.inputs.items().map { it }.sortedBy { it.key }.forEach { (name, input) ->
                children.add(DataItem(name, input, "input.png"))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is CompoundInput) {
                children.add(DataItem(name, resource, "input.png"))
                updateLabel()
            }
        }

        override fun toString() = "Inputs (${children.size})"

    }

    inner class ScenesDirectoryItem(label: String, val directory: File)
        : TopLevelItem(label, "folder.png") {

        init {
            val directoryLister = FileLister(onlyFiles = false)
            directoryLister.listFiles(directory).forEach { file ->
                children.add(ScenesDirectoryItem(file.name, file))
            }
            val sceneLister = FileLister(extensions = listOf("scene"))
            sceneLister.listFiles(directory).forEach { file ->
                children.add(SceneItem(file))
            }
            isExpanded = true
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is File && resource.parentFile == directory) {
                if (resource.isDirectory) {
                    children.add(ScenesDirectoryItem(resource.name, resource))
                } else if (resource.extension == "scene") {
                    children.add(SceneItem(resource))
                }
            }
        }

        override fun isLeaf() = false

    }

    inner class SceneItem(val file: File) : DataItem(file.nameWithoutExtension, SceneStub(file), "scene.png") {
        override fun resourceRemoved(resource: Any, name: String) {
            if (resource is File && resource == file) {
                parent?.let {
                    (it as ResourceItem).remove(this)
                }
            } else {
                super.resourceRemoved(resource, name)
            }
        }
    }
}

class SceneStub(val file: File) {
    override fun equals(other: Any?): Boolean {
        if (other is SceneStub) {
            return file == other.file
        }
        return false
    }
}

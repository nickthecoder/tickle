package uk.co.nickthecoder.tickle.editor

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.util.*
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Layout
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.ResourcesListener
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable
import java.io.File

class ResourcesTree()

    : TreeView<String>() {

    val resources = Resources.instance

    init {
        isEditable = false
        root = RootItem()
        isShowRoot = false
        root.children
        addEventFilter(MouseEvent.MOUSE_RELEASED) { onMousePressed(it) }
        addEventFilter(MouseEvent.MOUSE_PRESSED) { onMousePressed(it) }
        addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }

        root.isExpanded = true
    }

    fun onMousePressed(event: MouseEvent) {
        if (event.clickCount == 2) {
            editItem()
            event.consume()
        }
        if (event.isPopupTrigger) {
            Platform.runLater {
                val item = selectionModel.selectedItem
                if (item != null && item is ResourceItem) {
                    val menu = item.createContextMenu()
                    menu.show(MainWindow.instance.stage, event.screenX, event.screenY)
                }
            }

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

            MainWindow.instance.openTab(item.name, data)

        } else {
            item.isExpanded = !item.isExpanded
        }
    }

    abstract inner class ResourceItem(label: String = "", val resourceType: ResourceType)

        : TreeItem<String>(label) {

        open fun data(): Any? = null

        override fun isLeaf() = true

        open fun removed() {}

        fun createContextMenu(): ContextMenu {
            val menu = ContextMenu()
            newMenuItem()?.let { menu.items.add(it) }
            renameMenuItem()?.let { menu.items.add(it) }
            deleteMenuItem()?.let { menu.items.add(it) }

            return menu
        }

        open fun newMenuItem(): MenuItem? {
            if (resourceType.canCreate) {
                val menuItem = MenuItem("New ${resourceType.label}")
                menuItem.onAction = EventHandler {
                    TaskPrompter(NewResourceTask(resourceType)).placeOnStage(Stage())
                }
                return menuItem
            } else {
                return null
            }
        }

        open fun deleteMenuItem(): MenuItem? = null

        open fun renameMenuItem(): MenuItem? = null

        init {
            graphic = ImageView(EditorAction.imageResource(resourceType.graphicName))
        }

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


    inner class RootItem : ResourceItem(resources.file.nameWithoutExtension, ResourceType.ANY) {

        init {
            children.addAll(
                    GameInfoItem(),
                    EditorPreferencesItem(),
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

    inner class GameInfoItem() : DataItem("Game Info", resources.gameInfo, ResourceType.GAME_INFO) {

        override fun data(): GameInfo = resources.gameInfo
    }

    inner class EditorPreferencesItem() : DataItem("Editor Preferences", resources.gameInfo, ResourceType.PREFERENCES) {

        override fun data(): EditorPreferences = resources.preferences
    }

    open inner class DataItem(var name: String, val data: Any, resourceType: ResourceType, graphic: Node? = null)

        : ResourceItem(name, resourceType), ResourcesListener {

        init {
            resources.listeners.add(this)
            if (graphic != null) {
                this.graphic = graphic
            }
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

        override fun deleteMenuItem(): MenuItem? {
            if (data is Deletable) {
                val menuItem = MenuItem("Delete ${value}")
                menuItem.onAction = EventHandler {
                    data.delete()
                }
                return menuItem
            }
            return null
        }

        override fun renameMenuItem(): MenuItem? {
            if (data is Renamable) {
                val menuItem = MenuItem("Rename")
                menuItem.onAction = EventHandler {
                    val oldName = resources.findName(data)
                    if (oldName != null) {
                        TaskPrompter(RenameResourceTask(data, resourceType, oldName)).placeOnStage(Stage())
                    }
                }
                return menuItem
            }
            return null
        }
    }

    abstract inner class TopLevelItem(label: String = "", resourceType: ResourceType)

        : ResourceItem(label, resourceType), ResourcesListener {

        init {
            resources.listeners.add(this)
        }

        override fun isLeaf() = false

        override fun removed() {
            resources.listeners.remove(this)
        }

    }

    inner class TexturesItem() : TopLevelItem("Textures", ResourceType.TEXTURE) {

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

        : DataItem(name, texture, ResourceType.TEXTURE) {

        init {
            resources.poses.items().filter { it.value.texture === texture }.map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(DataItem(name, pose, ResourceType.POSE, wrappedThumbnail(pose)))
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
                    children.add(DataItem(name, resource, ResourceType.POSE))
                }
            }
        }

        override fun isLeaf() = false
    }

    inner class PosesItem() : TopLevelItem("Poses", ResourceType.POSE) {

        init {
            resources.poses.items().map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(DataItem(name, pose, ResourceType.POSE, wrappedThumbnail(pose)))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Pose) {
                children.add(DataItem(name, resource, ResourceType.POSE, wrappedThumbnail(resource)))
                updateLabel()
            }
        }

        override fun toString() = "All Poses (${children.size})"
    }


    inner class FontResourcesItem() : TopLevelItem("Fonts", ResourceType.FONT) {

        init {
            resources.fontResources.items().map { it }.sortedBy { it.key }.forEach { (name, fontResource) ->
                children.add(DataItem(name, fontResource, ResourceType.FONT))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is FontResource) {
                children.add(DataItem(name, resource, ResourceType.FONT))
                updateLabel()
            }
        }

        override fun toString() = "Fonts (${children.size})"
    }


    inner class CostumesItem() : TopLevelItem("Costumes", ResourceType.COSTUME) {

        init {
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

    inner class CostumeGroupItem(name: String, val costumeGroup: CostumeGroup)

        : DataItem(name, costumeGroup, ResourceType.COSTUME_GROUP) {

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

        override fun isLeaf() = false

        override fun toString() = "$name (${children.size})"
    }

    fun wrappedThumbnail(costume: Costume) = wrappedThumbnail(costume.editorPose())

    /**
     * Wrap the thumbnail image in a box, so that all the labels line up, despite the images being different
     * widths (or null).
     */
    fun wrappedThumbnail(pose: Pose?): Node {
        val box = HBox()
        box.prefWidth = resources.preferences.treeThumnailSize.toDouble()
        val iv = pose?.thumbnail(resources.preferences.treeThumnailSize)
        iv?.let { box.children.add(it) }
        return box
    }

    inner class CostumeItem(name: String, val costume: Costume, val costumeGroup: CostumeGroup?)

        : DataItem(name, costume, ResourceType.COSTUME, wrappedThumbnail(costume)) {

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
                if (resources.findCostumeGroup(name) != null) {
                    parent?.let {
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

    inner class LayoutsItem() : TopLevelItem("Layout", ResourceType.LAYOUT) {

        init {
            resources.layouts.items().map { it }.sortedBy { it.key }.forEach { (name, layout) ->
                children.add(DataItem(name, layout, ResourceType.LAYOUT))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Layout) {
                children.add(DataItem(name, resource, ResourceType.LAYOUT))
                updateLabel()
            }
        }

        override fun toString() = "Layouts (${children.size})"

    }

    inner class InputsItem() : TopLevelItem("Inputs", ResourceType.INPUT) {

        init {
            resources.inputs.items().map { it }.sortedBy { it.key }.forEach { (name, input) ->
                children.add(DataItem(name, input, ResourceType.INPUT))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is CompoundInput) {
                children.add(DataItem(name, resource, ResourceType.INPUT))
                updateLabel()
            }
        }

        override fun toString() = "Inputs (${children.size})"

    }

    inner class ScenesDirectoryItem(label: String, val directory: File)
        : TopLevelItem(label, ResourceType.SCENE_DIRECTORY) {

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

    inner class SceneItem(val file: File) : DataItem(file.nameWithoutExtension, SceneStub(file), ResourceType.SCENE) {
        override fun resourceRemoved(resource: Any, name: String) {
            if (resource is File && resource == file) {
                parent?.let {
                    (it as ResourceItem).remove(this)
                }
            } else {
                super.resourceRemoved(resource, name)
            }
        }

        override fun deleteMenuItem(): MenuItem? {
            val menuItem = MenuItem("Delete")
            menuItem.onAction = EventHandler {
                TaskPrompter(DeleteSceneTask(file)).placeOnStage(Stage())
            }
            return menuItem
        }

        override fun renameMenuItem(): MenuItem? {
            val menuItem = MenuItem("Rename")
            menuItem.onAction = EventHandler {
                TaskPrompter(RenameSceneTask(file)).placeOnStage(Stage())
            }
            return menuItem
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

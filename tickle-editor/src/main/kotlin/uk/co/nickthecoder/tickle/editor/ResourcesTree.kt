/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.editor

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.resources.DesignResources
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.editor.util.*
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import uk.co.nickthecoder.tickle.sound.Sound
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable
import java.io.File

class ResourcesTree

    : TreeView<String>() {

    val resources
        get() = Resources.instance as DesignResources

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

    fun reload() {
        (root as RootItem).reload()
    }

    fun onMousePressed(event: MouseEvent) {
        if (event.eventType == MouseEvent.MOUSE_PRESSED && event.clickCount == 2) {
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
        val item = selectionModel.selectedItem ?: return

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

        open val newResourceType: ResourceType? = resourceType

        fun createContextMenu(): ContextMenu {
            val menu = ContextMenu()
            renameMenuItem()?.let { menu.items.add(it) }
            deleteMenuItem()?.let { menu.items.add(it) }

            newResourceType?.let { newResourceType ->
                val newItem = MenuItem("New ${newResourceType.label}")
                newItem.onAction = EventHandler {
                    TaskPrompter(NewResourceTask(newResourceType)).placeOnStage(Stage())
                }
                menu.items.add(newItem)
            }

            val newMenu = Menu("New")
            ResourceType.values().filter { it.canCreate() }.forEach { resourceType ->
                val newItem = MenuItem(resourceType.label)
                newItem.onAction = EventHandler {
                    TaskPrompter(NewResourceTask(resourceType)).placeOnStage(Stage())
                }
                newMenu.items.add(newItem)
            }
            menu.items.add(newMenu)
            return menu
        }

        open fun deleteMenuItem(): MenuItem? = null

        open fun renameMenuItem(): MenuItem? = null

        init {
            graphic = ImageView(EditorAction.imageResource(resourceType.graphicName))
        }

        open fun add(child: ResourceItem) {
            var i = 0
            while (i < children.size && (children[i] as ResourceItem).value < child.value) {
                i++
            }
            children.add(i, child)
            updateLabel()
        }

        fun remove(child: ResourceItem) {
            children.remove(child)
            updateLabel()
        }

        fun updateLabel() {
            value = toString()
        }

        override fun toString(): String = value
    }


    inner class RootItem :
            ResourceItem(resources.file.nameWithoutExtension, ResourceType.ANY),
            ResourcesListener {

        init {
            resources.listeners.add(this)
            reload()
        }

        fun reload() {
            children.clear()
            children.addAll(
                    GameInfoItem(),
                    EditorPreferencesItem(),
                    APIDocumentationItem(),
                    TexturesItem(),
                    PosesItem(),
                    FontResourcesItem(),
                    SoundsItem(),
                    CostumesItem(),
                    InputsItem(),
                    LayoutsItem(),
                    ScenesDirectoryItem("Scenes", resources.sceneDirectory.absoluteFile),
                    FXCoderDirectory()
            )

            if (ScriptManager.languages().isNotEmpty()) {
                children.add(ScriptsItem())
            }
        }

        override val newResourceType = null

        override fun isLeaf() = false

        override fun resourceAdded(resource: Any, name: String) {

            fun scan(parent: ResourceItem) {
                for (child in parent.children) {
                    if (child is ResourceItem) {
                        scan(child)
                    }
                    if (child is ResourcesListener) {
                        child.resourceAdded(resource, name)
                    }
                }
            }
            scan(this)
        }

        override fun resourceRemoved(resource: Any, name: String) {

            fun scan(parent: ResourceItem) {
                for (child in parent.children) {
                    if (child is ResourceItem) {
                        scan(child)
                    }
                    if (child is DataItem && child.data == resource) {
                        parent.remove(child)
                        return
                    }
                }
            }
            scan(this)
        }

        override fun resourceChanged(resource: Any) {

            fun scan(parent: ResourceItem) {
                for (child in parent.children) {
                    if (child is ResourceItem) {
                        scan(child)
                    }
                    if (child is DataItem && child.data == resource) {
                        child.resourceChanged(resource)
                        return
                    }
                }
            }
            scan(this)
        }

        override fun resourceRenamed(resource: Any, oldName: String, newName: String) {

            fun scan(parent: ResourceItem) {
                for (child in parent.children) {
                    if (child is ResourceItem) {
                        scan(child)
                    }
                    if (child is DataItem && child.data == resource) {
                        parent.remove(child)
                        child.name = newName
                        child.updateLabel()
                        parent.add(child)
                        return
                    }
                }
            }
            scan(this)
        }

    }

    inner class GameInfoItem() : DataItem("Game Info", resources.gameInfo, ResourceType.GAME_INFO) {

        override fun data(): GameInfo = resources.gameInfo

        override val newResourceType = null

    }

    inner class APIDocumentationItem() : DataItem("API Documentation", APIStub, ResourceType.API_Documentation) {

        override val newResourceType = null

    }

    inner class EditorPreferencesItem() : DataItem("Editor Preferences", resources.gameInfo, ResourceType.PREFERENCES) {

        override fun data(): EditorPreferences = resources.preferences

        override val newResourceType = null

    }

    open inner class DataItem(var name: String, val data: Any, resourceType: ResourceType, graphic: Node? = null)

        : ResourceItem(name, resourceType), ResourcesListener {

        init {
            if (graphic != null) {
                this.graphic = graphic
            }
        }

        override fun toString() = name

        override fun data() = data

        override fun deleteMenuItem(): MenuItem? {
            if (data is Deletable) {
                val menuItem = MenuItem("Delete ${value}")
                menuItem.onAction = EventHandler {
                    data.deletePrompted(name)
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

        override fun isLeaf() = false

    }

    inner class TexturesItem() : TopLevelItem("Textures", ResourceType.TEXTURE) {

        init {
            resources.textures.items().map { it }.sortedBy { it.key }.forEach { (name, texture) ->
                children.add(TextureItem(name, texture))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Texture) {
                add(TextureItem(name, resource))
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
                    add(PoseItem(name, resource))
                }
            }
        }

        override fun isLeaf() = false
    }

    inner class PosesItem : TopLevelItem("Poses", ResourceType.POSE) {

        init {
            resources.poses.items().map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(PoseItem(name, pose))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Pose) {
                add(PoseItem(name, resource))
            }
        }

        override fun toString() = "All Poses (${children.size})"
    }

    inner class PoseItem(name: String, val pose: Pose) : DataItem(name, pose, ResourceType.POSE, wrappedThumbnail(pose)) {

        override fun resourceChanged(resource: Any) {
            if (resource === data) {
                graphic = wrappedThumbnail(pose)
            }
        }
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
                add(DataItem(name, resource, ResourceType.FONT))
            }
        }

        override fun toString() = "Fonts (${children.size})"
    }


    inner class SoundsItem() : TopLevelItem("Sounds", ResourceType.SOUND) {

        init {
            resources.sounds.items().map { it }.sortedBy { it.key }.forEach { (name, sound) ->
                children.add(DataItem(name, sound, ResourceType.SOUND))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Sound) {
                add(DataItem(name, resource, ResourceType.SOUND))
            }
        }

        override fun toString() = "Sounds (${children.size})"
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
                add(CostumeGroupItem(name, resource))
            }
            if (resource is Costume && resource.costumeGroup == null) {
                add(CostumeItem(name, resource, null))
            }
        }

        override fun add(child: ResourceItem) {
            if (child is CostumeGroupItem) add(child)
            if (child is CostumeItem) add(child)
        }

        fun add(child: CostumeGroupItem) {
            var i = 0
            while (i < children.size && children[i] is CostumeGroupItem && children[i].value < child.value) {
                i++
            }
            children.add(i, child)
            updateLabel()
        }

        fun add(child: CostumeItem) {
            var i = 0
            while (i < children.size && children[i] is CostumeGroupItem) {
                i++
            }
            while (i < children.size && children[i].value < child.value) {
                i++
            }
            children.add(i, child)
            updateLabel()
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
                add(CostumeItem(name, resource, costumeGroup))
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

        override fun resourceChanged(resource: Any) {
            if (resource === costume) {
                if (costumeGroup != resource.costumeGroup) {
                    // Costume has changed groups, so remove this
                    parent.children.remove(this)
                }
            }
        }

        override fun resourceRemoved(resource: Any, name: String) {
            if (resource === costume) {
                if (!exists(resource)) {
                    parent?.let { (it as ResourceItem).remove(this) }
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

    inner class LayoutsItem
        : TopLevelItem("Layout", ResourceType.LAYOUT) {

        init {
            resources.layouts.items().map { it }.sortedBy { it.key }.forEach { (name, layout) ->
                children.add(DataItem(name, layout, ResourceType.LAYOUT))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is Layout) {
                add(DataItem(name, resource, ResourceType.LAYOUT))
            }
        }

        override fun toString() = "Layouts (${children.size})"

    }

    inner class InputsItem
        : TopLevelItem("Inputs", ResourceType.INPUT) {

        init {
            resources.inputs.items().map { it }.sortedBy { it.key }.forEach { (name, input) ->
                children.add(DataItem(name, input, ResourceType.INPUT))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is CompoundInput) {
                add(DataItem(name, resource, ResourceType.INPUT))
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
            isExpanded = false
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is File && resource.parentFile == directory) {
                if (resource.isDirectory) {
                    add(ScenesDirectoryItem(resource.name, resource))
                } else if (resource.extension == "scene") {
                    add(SceneItem(resource))
                }
            }
        }

        fun add(child: ScenesDirectoryItem) {
            var i = 0
            while (i < children.size && children[i] is ScenesDirectoryItem && children[i].value < child.value) {
                i++
            }
            children.add(i, child)
            updateLabel()
        }

        fun add(child: SceneItem) {
            var i = 0
            while (i < children.size && children[i] is ScenesDirectoryItem) {
                i++
            }
            while (i < children.size && children[i].value < child.value) {
                i++
            }
            children.add(i, child)
            updateLabel()
        }

        override fun isLeaf() = false

        override val newResourceType = ResourceType.SCENE

    }

    inner class SceneItem(val file: File)
        : DataItem(file.nameWithoutExtension, SceneStub(file), ResourceType.SCENE) {

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


    inner class ScriptsItem : TopLevelItem("Scripts", ResourceType.SCRIPT_DIRECTORY) {

        var scriptDirectory = resources.scriptDirectory()

        init {
            val lister = FileLister(extensions = ScriptManager.languages().map { it.fileExtension })
            lister.listFiles(scriptDirectory).forEach { file ->
                children.add(ScriptItem(file))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is File && resource.parentFile == scriptDirectory) {
                children.add(ScriptItem(resource))
                updateLabel()
            }
        }

        override val newResourceType = ResourceType.SCRIPT

        override fun isLeaf() = false

        override fun toString() = "Scripts (${children.size})"

    }

    inner class ScriptItem(val file: File)

        : DataItem(file.nameWithoutExtension, ScriptStub(file), ResourceType.SCRIPT) {

        override fun resourceRemoved(resource: Any, name: String) {
            if (resource is File && resource == file) {
                parent?.let {
                    (it as ResourceItem).remove(this)
                    updateLabel()
                }
            } else {
                super.resourceRemoved(resource, name)
            }
        }

        override fun deleteMenuItem(): MenuItem? {
            val menuItem = MenuItem("Delete")
            menuItem.onAction = EventHandler {
                val name = file.nameWithoutExtension

                var scriptKlass: Class<*>? = null
                try {
                    scriptKlass = ScriptManager.classForName(name)
                } catch (e: ClassNotFoundException) {
                }

                if (scriptKlass != null) {
                    if (Role::class.java.isAssignableFrom(scriptKlass)) {
                        resources.costumes.items().values.filter { it.roleString == name }.forEach {
                            it.roleString = ""
                        }
                    } else if (Producer::class.java.isAssignableFrom(scriptKlass)) {

                        if (resources.gameInfo.producerString == name) {
                            resources.gameInfo.producerString = NoProducer::class.java.name
                        }
                    } else if (Director::class.java.isAssignableFrom(scriptKlass)) {
                        // TODO Iterate over all scenes, and reset matching Directors to NoDirector`
                    }
                }

                file.delete()
                resources.fireRemoved(file, name)
                resources.save() // In case a costume was altered due to the delete.
            }
            return menuItem
        }

        override fun renameMenuItem(): MenuItem? {
            val menuItem = MenuItem("Rename")
            menuItem.onAction = EventHandler {
                TaskPrompter(RenameScriptTask(file)).placeOnStage(Stage())
            }
            return menuItem
        }
    }


    inner class FXCoderDirectory : TopLevelItem("FXCoder", ResourceType.FXCODER_DIRECTORY) {

        var fxCoderDirectory = resources.fxcoderDirectory()

        init {
            val lister = FileLister(extensions = listOf("groovy"))
            lister.listFiles(fxCoderDirectory).forEach { file ->
                children.add(FXCoderItem(file))
            }
            updateLabel()
        }

        override fun resourceAdded(resource: Any, name: String) {
            if (resource is File && resource.parentFile == fxCoderDirectory) {
                children.add(FXCoderItem(resource))
                updateLabel()
            }
        }

        override val newResourceType = ResourceType.FXCODER

        override fun isLeaf() = false

        override fun toString() = "FXCoder (${children.size})"

    }

    inner class FXCoderItem(val file: File)

        : DataItem(file.nameWithoutExtension, FXCoderStub(file), ResourceType.FXCODER) {

        override fun resourceRemoved(resource: Any, name: String) {
            if (resource is File && resource == file) {
                parent?.let {
                    (it as ResourceItem).remove(this)
                    updateLabel()
                }
            } else {
                super.resourceRemoved(resource, name)
            }
        }

        override fun deleteMenuItem(): MenuItem? {
            val menuItem = MenuItem("Delete")
            menuItem.onAction = EventHandler {
                val name = file.nameWithoutExtension
                file.delete()
                resources.fireRemoved(file, name)
            }
            return menuItem
        }

        /*
        override fun renameMenuItem(): MenuItem? {
            val menuItem = MenuItem("Rename")
            menuItem.onAction = EventHandler {
                TaskPrompter(RenameScriptTask(file)).placeOnStage(Stage())
            }
            return menuItem
        }
        */
    }
}


class ScriptStub(val file: File) {

    val name: String
        get() = file.absoluteFile.toRelativeString(Resources.instance.scriptDirectory())


    override fun equals(other: Any?): Boolean {
        if (other is ScriptStub) {
            return file == other.file
        }
        return false
    }

    override fun hashCode() = file.hashCode() + 2
}

class FXCoderStub(val file: File) {

    val name: String
        get() {
            return file.absoluteFile.toRelativeString(Resources.instance.fxcoderDirectory())
        }

    override fun equals(other: Any?): Boolean {
        if (other is FXCoderStub) {
            return file == other.file
        }
        return false
    }

    override fun hashCode() = file.hashCode() + 3
}

object APIStub {}

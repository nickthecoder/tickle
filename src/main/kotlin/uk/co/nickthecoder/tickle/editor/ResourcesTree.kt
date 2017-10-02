package uk.co.nickthecoder.tickle.editor

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.tabs.*
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input

class ResourcesTree(val mainWindow: MainWindow)

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
        val i = selectionModel.selectedItem
        if (i == null) return

        val item = i as ResourceItem

        if (item.isLeaf) {

            val data = item.data()

            if (data != null) {
                val tab = mainWindow.findTab(data)
                if (tab == null) {
                    val newTab = createTab(item.value, data)
                    if (newTab != null) {
                        mainWindow.tabPane.add(newTab)
                        newTab.isSelected = true
                    }
                } else {
                    tab.isSelected = true
                }
            }
        } else {

            item.isExpanded = !item.isExpanded

        }
    }

    fun createTab(name: String, data: Any): EditorTab? {

        if (data is GameInfo) {
            return GameInfoTab()

        } else if (data is TextureResource) {
            return TextureTab(name, data)

        } else if (data is Pose) {
            return PoseTab(name, data)

        } else if (data is Layout) {
            return LayoutTab(name, data)

        } else if (data is CompoundInput) {
            return InputTab(name, data)

        } else if (data is Costume) {
            return CostumeTab(name, data)
        }

        return null
    }

    abstract inner class ResourceItem(label: String = "") : TreeItem<String>(label) {
        open fun data(): Any? = null

        override fun isLeaf() = true

        open fun removed() {}
    }


    inner class RootItem() : ResourceItem("Resources") {

        init {
            children.addAll(GameInfoItem(), TexturesItem(), PosesItem(), CostumesItem(), InputsItem(), LayoutsItem(), ScenesItem())
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

    inner class DataItem(val name: String, val data: Any) : ResourceItem(name), ResourcesListener {

        init {
            Resources.instance.listeners.add(this)
        }

        override fun data() = data

        override fun resourceRenamed(resource: Any, oldName: String, newName: String) {
            if (resource === data) {
                value = newName
            }
        }

        override fun removed() {
            Resources.instance.listeners.remove(this)
        }
    }

    abstract inner class TopLevelItem(val type: Class<*>) : ResourceItem(), ResourcesListener {
        init {
            Resources.instance.listeners.add(this)
        }

        override fun isLeaf() = false

        fun maybeRebuild(resource: Any) {
            if (type.isInstance(resource)) {
                rebuildChildren()
            }
        }

        override fun resourceRemoved(resource: Any, name: String) {
            maybeRebuild(resource)
        }

        override fun resourceAdded(resource: Any, name: String) {
            maybeRebuild(resource)
        }

        open fun rebuildChildren() {
            children.forEach { child ->
                (child as ResourceItem).removed()
            }
            children.clear()
        }

        override fun removed() {
            Resources.instance.listeners.remove(this)
        }
    }

    inner class TexturesItem() : TopLevelItem(TextureResource::class.java) {

        init {
            rebuildChildren()
            isExpanded = true
        }

        override fun rebuildChildren() {
            super.rebuildChildren()
            resources.textures().map { it }.sortedBy { it.key }.forEach { (name, texture) ->
                children.add(DataItem(name, texture))
            }
            value = toString()
        }

        override fun toString() = "Textures (${children.size})"
    }


    inner class PosesItem() : TopLevelItem(Pose::class.java) {

        init {
            rebuildChildren()
        }

        override fun rebuildChildren() {
            super.rebuildChildren()
            resources.poses().map { it }.sortedBy { it.key }.forEach { (name, pose) ->
                children.add(DataItem(name, pose))
            }
            value = toString()
        }

        override fun toString() = "Poses (${children.size})"
    }


    inner class CostumesItem() : TopLevelItem(Costume::class.java) {

        init {
            rebuildChildren()
        }

        override fun rebuildChildren() {
            super.rebuildChildren()
            resources.costumes().map { it }.sortedBy { it.key }.forEach { (name, costume) ->
                children.add(DataItem(name, costume))
            }
            value = toString()
        }

        override fun toString() = "Costumes (${children.size})"

    }

    inner class LayoutsItem() : TopLevelItem(Layout::class.java) {

        init {
            isExpanded = true
            rebuildChildren()
        }

        override fun rebuildChildren() {
            super.rebuildChildren()
            resources.layouts().map { it }.sortedBy { it.key }.forEach { (name, layout) ->
                children.add(DataItem(name, layout))
            }
            value = toString()
        }

        override fun toString() = "Layouts (${children.size})"

    }


    inner class InputsItem() : TopLevelItem(Input::class.java) {

        init {
            rebuildChildren()
        }

        override fun rebuildChildren() {
            super.rebuildChildren()
            resources.inputs().map { it }.sortedBy { it.key }.forEach { (name, input) ->
                children.add(DataItem(name, input))
            }
            value = toString()
        }

        override fun toString() = "Inputs (${children.size})"

    }


    inner class ScenesItem() : ResourceItem("Scenes") {

        init {
            // TODO Create a hierarchy of directories and scene files
            // Could use Resources listeners to update the tree, even though scenes aren't in Resources.
            val lister = FileLister(depth = 3, extensions = listOf("scene"))
            lister.listFiles(resources.sceneDirectory).forEach { file ->
                children.add(DataItem(file.name, file))
            }
        }

        override fun isLeaf() = false
    }

}

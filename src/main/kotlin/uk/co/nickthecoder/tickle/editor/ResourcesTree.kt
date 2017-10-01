package uk.co.nickthecoder.tickle.editor

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.Input
import java.io.File

class ResourcesTree(val mainWindow: MainWindow, val resources: Resources)

    : TreeView<String>() {

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
        val item = selectionModel.selectedItem as ResourceItem

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
            return GameInfoTab(resources)

        } else if (data is TextureResource) {
            return TextureTab(resources, name, data)

        } else if (data is Pose) {
            return PoseTab(resources, name, data)

        } else if (data is Layout) {
            return LayoutTab(resources, name, data)
        }
        return null
    }

    abstract inner class ResourceItem(label: String) : TreeItem<String>(label) {
        open fun data(): Any? = null
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
                children.add(PoseItem(name, pose))
            }
        }

        override fun data(): GameInfo = resources.gameInfo
        override fun isLeaf() = true
    }


    inner class TexturesItem() : ResourceItem("Textures") {

        init {
            resources.textures().forEach { name, texture ->
                children.add(TextureItem(name, texture))
            }
            isExpanded = true
        }

        override fun isLeaf() = false
    }

    inner class TextureItem(val name: String, val textureResource: TextureResource) : ResourceItem(name) {

        override fun data() = textureResource
        override fun isLeaf() = true
    }


    inner class PosesItem() : ResourceItem("Poses") {

        init {
            resources.poses().forEach { name, pose ->
                children.add(PoseItem(name, pose))
            }
        }

        override fun isLeaf() = false
    }

    inner class PoseItem(val name: String, val pose: Pose) : ResourceItem(name) {

        override fun data() = pose
        override fun isLeaf() = true
    }


    inner class CostumesItem() : ResourceItem("Costumes") {

        init {
            resources.costumes().forEach { name, costume ->
                children.add(CostumeItem(name, costume))
            }
        }

        override fun isLeaf() = false
    }


    inner class CostumeItem(val name: String, val costume: Costume) : ResourceItem(name) {

        override fun data() = costume
        override fun isLeaf() = true
    }

    inner class LayoutsItem() : ResourceItem("Layouts") {

        init {
            resources.layouts().forEach { name, layout ->
                children.add(LayoutItem(name, layout))
            }
            isExpanded = true
        }

        override fun isLeaf() = false
    }


    inner class LayoutItem(val name: String, val layout: Layout) : ResourceItem(name) {

        override fun data() = layout
        override fun isLeaf() = true
    }

    inner class InputsItem() : ResourceItem("Inputs") {

        init {
            resources.inputs().forEach { name, input ->
                children.add(InputItem(name, input))
            }
        }

        override fun isLeaf() = false
    }


    inner class InputItem(val name: String, val input: Input) : ResourceItem(name) {

        override fun data() = input
        override fun isLeaf() = true
    }

    inner class ScenesItem() : ResourceItem("Scenes") {

        init {
            val lister = FileLister(depth = 3, extensions = listOf("scene"))
            lister.listFiles(resources.sceneDirectory).forEach { file ->
                children.add(SceneItem(file))
            }
        }

        override fun isLeaf() = false
    }


    inner class SceneItem(val file: File) : ResourceItem(file.nameWithoutExtension) {

        override fun data() = file
        override fun isLeaf() = true
    }

}

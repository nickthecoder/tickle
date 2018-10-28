package uk.co.nickthecoder.tickle.editor

import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.Scene
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.Modality
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.resources.Resources
import java.io.File

class OpenResource(parentStage: Stage) : Stage() {

    private val borderPane = BorderPane()

    private val textField = TextField()

    private val resources = Resources.instance

    private val shortcuts = ShortcutHelper("OpenResource", borderPane)

    init {
        textField.prefColumnCount = 20

        title = "Open Resource"
        with(borderPane) {
            center = textField
        }

        with(shortcuts) {
            add(EditorActions.ESCAPE) { hide() }
        }

        scene = Scene(borderPane)

        textField.textProperty().addListener { _, _, _ -> textChanged() }

        initOwner(parentStage);
        initModality(Modality.APPLICATION_MODAL);
        showAndWait()
    }

    private fun textChanged() {
        val list = mutableListOf<Pair<String, ResourceType>>()
        val upperSearch = textField.text.toUpperCase()

        fun matches(name: String): Boolean = name.toUpperCase().contains(upperSearch)

        textField.contextMenu?.hide()
        // If I don't create a new context menu every time, then when a menu is too big, and is scrolled, then
        // the scroll position is remembered the next time (and the menu appears empty). Bug in JavaFX. Grr.
        textField.contextMenu = ContextMenu()

        if (upperSearch.isNotBlank()) {
            for (resourceType in ResourceType.values()) {

                fun addMatches(names: Set<String>) {
                    for (name in names) {
                        if (matches(name)) {
                            list.add(Pair(name, resourceType))
                        }
                    }
                }

                fun addFiles(dir: File, ext: String) {
                    val lister = FileLister(10, onlyFiles = true, extensions = listOf(ext))
                    lister.listFiles(dir).forEach { file ->
                        if (matches(file.nameWithoutExtension)) {
                            val file2 = file.relativeToOrSelf(resources.file.parentFile.absoluteFile)
                            val adjustedName = when (resourceType) {
                                ResourceType.SCRIPT -> file2.path.removePrefix("scripts${File.separatorChar}")
                                ResourceType.SCENE -> file2.path.removePrefix("scenes${File.separatorChar}").removeSuffix(".scene")
                                else -> file2.path
                            }
                            list.add(Pair(adjustedName, resourceType))
                        }
                    }
                }

                when (resourceType) {
                    ResourceType.POSE -> addMatches(resources.poses.items().keys)
                    ResourceType.COSTUME -> addMatches(resources.costumes.items().keys)
                    ResourceType.COSTUME_GROUP -> addMatches(resources.costumeGroups.items().keys)
                    ResourceType.TEXTURE -> addMatches(resources.textures.items().keys)
                    ResourceType.FONT -> addMatches(resources.fontResources.items().keys)
                    ResourceType.SOUND -> addMatches(resources.sounds.items().keys)
                    ResourceType.INPUT -> addMatches(resources.inputs.items().keys)
                    ResourceType.LAYOUT -> addMatches(resources.layouts.items().keys)

                    ResourceType.SCENE -> addFiles(resources.sceneDirectory, "scene")
                    ResourceType.SCRIPT -> addFiles(resources.scriptDirectory(), "groovy")
                    ResourceType.FXCODER -> addFiles(resources.fxcoderDirectory(), "groovy")

                    else -> Unit
                }

            }
        }

        textField.contextMenu.items.clear()
        list.forEach { (name, resourceType) ->
            val item = MenuItem(name, ImageView(EditorAction.imageResource(resourceType.graphicName)))
            item.onAction = EventHandler { selectItem(name, resourceType) }
            textField.contextMenu.items.add(item)
        }

        if (list.isNotEmpty()) {
            // Grr, I have to put the menu on the right, because it seems that context menus cannot be set to a
            // minimum size, and therefore a long menu would obscure the text field when using Side.BOTTOM or TOP.
            textField.contextMenu.show(textField, Side.RIGHT, 0.0, 0.0)
        }
    }

    private fun selectItem(name: String, resourceType: ResourceType) {
        val adjustedName = when (resourceType) {
            ResourceType.FXCODER -> name.removePrefix("fxcoder${File.separatorChar}")
            else -> name
        }

        MainWindow.instance.openNamedTab(adjustedName, resourceType)
        hide()
    }
}

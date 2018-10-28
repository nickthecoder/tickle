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
        val list = mutableListOf<MatchedResource>()
        val upperSearch = textField.text.toUpperCase()

        fun matchScore(name: String): Double {
            val index = name.toUpperCase().indexOf(upperSearch)
            if (index < 0) return Double.MAX_VALUE

            return index.toDouble() + (name.length - upperSearch.length) * 0.1
        }

        textField.contextMenu?.hide()
        // If I don't create a new context menu every time, then when a menu is too big, and is scrolled, then
        // the scroll position is remembered the next time (and the menu appears empty). Bug in JavaFX. Grr.
        textField.contextMenu = ContextMenu()

        if (upperSearch.isNotBlank()) {
            for (resourceType in ResourceType.values()) {

                fun addMatches(names: Set<String>) {
                    for (name in names) {
                        val score = matchScore(name)
                        if (score != Double.MAX_VALUE) {
                            list.add(MatchedResource(name, resourceType, score))
                        }
                    }
                }

                fun addFiles(dir: File, ext: String) {
                    val lister = FileLister(10, onlyFiles = true, extensions = listOf(ext))
                    lister.listFiles(dir).forEach { file ->
                        val score = matchScore(file.nameWithoutExtension)
                        if (score != Double.MAX_VALUE) {
                            val file2 = file.relativeToOrSelf(resources.file.parentFile.absoluteFile)
                            val adjustedName = when (resourceType) {
                                ResourceType.SCRIPT -> file2.path.removePrefix("scripts${File.separatorChar}")
                                ResourceType.SCENE -> file2.path.removePrefix("scenes${File.separatorChar}").removeSuffix(".scene")
                                else -> file2.path
                            }
                            list.add(MatchedResource(adjustedName, resourceType, score))
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
        list.sortedBy { it.score }.forEach { (name, resourceType) ->
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

    data class MatchedResource(val name: String, val resourceType: ResourceType, val score: Double)
}

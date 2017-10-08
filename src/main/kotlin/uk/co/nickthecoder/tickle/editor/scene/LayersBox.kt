package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.image.ImageView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import uk.co.nickthecoder.tickle.editor.EditorAction

class LayersBox(val layers: Layers) {

    val grid = GridPane()

    fun build(): Node {
        grid.vgap = 6.0
        grid.hgap = 6.0

        val visibilityCC = ColumnConstraints()
        val labelCC = ColumnConstraints()
        val lockedCC = ColumnConstraints()

        labelCC.hgrow = Priority.ALWAYS
        grid.columnConstraints.addAll(visibilityCC, labelCC, lockedCC)

        update()
        return grid
    }

    fun update() {

        val visibleImage = EditorAction.imageResource("layer-visible.png")
        val invisibleImage = EditorAction.imageResource("layer-invisible.png")

        val lockedImage = EditorAction.imageResource("layer-locked.png")
        val unlockedImage = EditorAction.imageResource("layer-unlocked.png")

        grid.children.clear()

        var y = 0
        layers.names().forEach { stageName ->
            layers.stageLayer(stageName)?.let { stageLayer ->
                val label = Label(stageName)

                val visibility = ToggleButton()
                visibility.isSelected = stageLayer.isVisible
                visibility.graphic = ImageView(visibleImage)
                visibility.onAction = EventHandler {
                    stageLayer.isVisible = visibility.isSelected
                    visibility.graphic = ImageView(if (stageLayer.isVisible) visibleImage else invisibleImage)
                }

                val locked = ToggleButton()
                locked.isSelected = stageLayer.isLocked
                locked.onAction = EventHandler {
                    stageLayer.isLocked = locked.isSelected
                    locked.graphic = ImageView(if (stageLayer.isLocked) lockedImage else unlockedImage)
                }

                locked.graphic = ImageView(lockedImage)

                grid.children.addAll(label, visibility, locked)

                GridPane.setConstraints(visibility, 0, y)
                GridPane.setConstraints(label, 1, y)
                GridPane.setConstraints(locked, 2, y)
                y++
            }
        }
    }

}

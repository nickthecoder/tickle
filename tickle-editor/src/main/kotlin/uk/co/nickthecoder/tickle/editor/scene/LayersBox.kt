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
        val invisibleImage = EditorAction.imageResource("layer-hidden.png")

        val lockedImage = EditorAction.imageResource("layer-locked.png")
        val unlockedImage = EditorAction.imageResource("layer-unlocked.png")

        grid.children.clear()

        var y = 0
        layers.names().forEach { stageName ->
            layers.stageLayer(stageName)?.let { stageLayer ->
                val label = Label(stageName)

                val visibility = ToggleButton()
                visibility.isSelected = stageLayer.isVisible
                visibility.graphic = ImageView(if (stageLayer.isVisible) visibleImage else invisibleImage)
                visibility.onAction = EventHandler {
                    stageLayer.isVisible = visibility.isSelected
                    visibility.graphic = ImageView(if (stageLayer.isVisible) visibleImage else invisibleImage)
                }

                val locked = ToggleButton()
                locked.isSelected = stageLayer.isLocked
                locked.graphic = ImageView(if (stageLayer.isLocked) lockedImage else unlockedImage)
                locked.onAction = EventHandler {
                    stageLayer.isLocked = locked.isSelected
                    locked.graphic = ImageView(if (stageLayer.isLocked) lockedImage else unlockedImage)
                }

                grid.children.addAll(label, visibility, locked)

                GridPane.setConstraints(visibility, 0, y)
                GridPane.setConstraints(label, 1, y)
                GridPane.setConstraints(locked, 2, y)
                y++
            }
        }
    }

}

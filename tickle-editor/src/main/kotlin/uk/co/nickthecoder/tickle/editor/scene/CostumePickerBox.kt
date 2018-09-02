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

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TitledPane
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.editor.util.thumbnail
import uk.co.nickthecoder.tickle.resources.ResourceMap
import uk.co.nickthecoder.tickle.resources.Resources

/**
 * Shows buttons with thumbnail images of a Costume's default pose. Click the button to then "stamp" it into the scene.
 */
class CostumePickerBox(val onSelect: (String) -> Unit) {

    val vbox = VBox()

    val scrollPane = ScrollPane(vbox)

    fun build(): Node {

        vbox.children.add(buildGroup(Resources.instance.costumes, false))

        Resources.instance.costumeGroups.items().forEach { groupName, costumeGroup ->
            if (costumeGroup.showInSceneEditor) {
                val pane = TitledPane(groupName, buildGroup(costumeGroup, true))
                pane.styleClass.add("pickGroup")
                pane.isAnimated = false // The animation is too slow, and there's no API to change the speed. Grr.
                vbox.children.add(pane)
            }
        }

        scrollPane.isFitToWidth = true

        return scrollPane
    }

    fun buildGroup(group: ResourceMap<Costume>, all: Boolean): Node {

        val poseButtons = FlowPane()
        val textButtons = VBox()
        val result = VBox()

        result.children.addAll(poseButtons, textButtons)

        poseButtons.styleClass.add("pickPose")
        textButtons.styleClass.add("pickText")
        textButtons.isFillWidth = false

        group.items().forEach { costumeName, costume ->
            if (costume.showInSceneEditor && (all || Resources.instance.findCostumeGroup(costumeName) == null)) {
                val pose = costume.editorPose()

                if (pose == null) {

                    if (costume.chooseTextStyle(costume.initialEventName) != null) {
                        textButtons.children.add(createButton(costumeName, costume, ImageView(EditorAction.imageResource("font.png")), true))
                    }

                } else {
                    pose.thumbnail(Resources.instance.preferences.costumePickerThumbnailSize)?.let { iv ->
                        poseButtons.children.add(createButton(costumeName, costume, iv, false))
                    }
                }
            }
        }
        return result
    }

    fun createButton(costumeName: String, costume: Costume, icon: Node, isFont: Boolean): Button {
        val button = Button()
        val roleName = costume.roleString.split(".").lastOrNull()

        val name = if (roleName != null && roleName.isNotBlank() && roleName.toLowerCase() != costumeName.toLowerCase()) {
            "$costumeName ($roleName)"
        } else {
            costumeName
        }

        if (isFont) {
            button.text = name
            button.maxWidth = Double.MAX_VALUE
        } else {
            val size = Resources.instance.preferences.costumePickerThumbnailSize + 4.0 // 2 pixel padding
            button.prefWidth = size
            button.prefHeight = size
            button.tooltip = Tooltip(name)
        }
        button.graphic = icon
        button.setOnAction { onSelect(costumeName) }

        return button
    }

}

package uk.co.nickthecoder.tickle.editor.scene

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TitledPane
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.editor.util.thumbnail
import uk.co.nickthecoder.tickle.resources.ResourceType
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
            val pane = TitledPane(groupName, buildGroup(costumeGroup, true))
            pane.isAnimated = false // The animation is too slow, and there's no API to change the speed. Grr.
            vbox.children.add(pane)
        }

        scrollPane.isFitToWidth = true

        return scrollPane
    }

    fun buildGroup(group: ResourceType<Costume>, all: Boolean): Node {

        val flowPane = FlowPane()

        group.items().forEach { costumeName, costume ->
            if (all || Resources.instance.findCostumeGroup(costumeName) == null) {
                costume.thumbnail(40.0)?.let { iv ->
                    val button = Button()
                    val roleName = costume.roleString.split(".").lastOrNull()
                    with(button) {
                        graphic = iv
                        prefWidth = 44.0
                        prefHeight = 44.0
                        padding = Insets(2.0)
                        if (roleName != null && roleName.isNotBlank() && roleName.toLowerCase() != costumeName.toLowerCase()) {
                            tooltip = Tooltip("$costumeName ($roleName)")
                        } else {
                            tooltip = Tooltip(costumeName)
                        }
                        setOnAction { onSelect(costumeName) }
                    }

                    flowPane.children.add(button)
                }
            }
        }
        return flowPane
    }

}

package uk.co.nickthecoder.tickle.editor

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.scene.thumbnail

class CostumesBox {

    val flowPane = FlowPane()

    val scrollPane = ScrollPane(flowPane)

    fun build(): Node {

        scrollPane.isFitToWidth = true

        Resources.instance.costumes().forEach { name, costume ->
            costume.thumbnail(40.0)?.let { iv ->
                val button = Button()
                val roleName = costume.roleString.split(".").lastOrNull()
                with(button) {
                    graphic = iv
                    prefWidth = 44.0
                    prefHeight = 44.0
                    padding = Insets(2.0)
                    if (roleName != null && roleName.isNotBlank() && roleName.toLowerCase() != name.toLowerCase()) {
                        tooltip = Tooltip("$name ($roleName)")
                    } else {
                        tooltip = Tooltip(name)
                    }
                    setOnAction { MainWindow.instance?.selectCostume(costume) }
                }

                flowPane.children.add(button)
            }

        }

        return scrollPane
    }

}

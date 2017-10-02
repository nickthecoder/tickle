package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.Node
import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.gui.MyTab

open class EditorTab(
        val dataType: String,
        val dataName: String,
        val data: Any,
        graphic: Node? = null)

    : MyTab("$dataType $dataName", Label("Empty"), graphic) {


}

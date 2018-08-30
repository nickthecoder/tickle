package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.Node
import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.gui.MyTab

open class EditorTab(
        val dataName: String,
        val data: Any,
        graphic: Node? = null)

    : MyTab(dataName, Label("Empty"), graphic) {


}

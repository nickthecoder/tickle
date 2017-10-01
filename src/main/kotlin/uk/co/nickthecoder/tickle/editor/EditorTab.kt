package uk.co.nickthecoder.tickle.editor

import javafx.scene.Node
import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.gui.MyTab

open class EditorTab(
        text: String = "",
        val data : Any,
        graphic: Node? = null)

    : MyTab(text, Label("Empty"), graphic) {


}

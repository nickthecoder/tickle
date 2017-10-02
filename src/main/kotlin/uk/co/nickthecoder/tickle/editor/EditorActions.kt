package uk.co.nickthecoder.tickle.editor

import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.gui.ApplicationAction


object EditorActions {

    val nameToActionMap = mutableMapOf<String, ApplicationAction>()

    val RESOURCES_SAVE = EditorAction("resources.save", KeyCode.S, control = true, tooltip = "Save Resources")
    val TAB_CLOSE = EditorAction("tab.close", KeyCode.W, control = true, label = "Close Tab")

}

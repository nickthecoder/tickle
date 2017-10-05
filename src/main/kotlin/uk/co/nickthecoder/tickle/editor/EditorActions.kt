package uk.co.nickthecoder.tickle.editor

import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.gui.ApplicationAction


object EditorActions {

    val nameToActionMap = mutableMapOf<String, ApplicationAction>()

    val RESOURCES_SAVE = EditorAction("resources.save", KeyCode.S, control = true, tooltip = "Save Resources")
    val NEW = EditorAction("new", KeyCode.N, control = true, tooltip = "Create a New Resource")
    val RUN = EditorAction("run", KeyCode.R, control = true, tooltip = "Run the game")

    val ACCORDION_RESOURCES = EditorAction("accordion.resources", KeyCode.F2)
    val ACCORDION_COSTUME = EditorAction("accordion.costume", KeyCode.F3)
    val ACCORDION_PROPERTIES = EditorAction("accordion.properties", KeyCode.F4)

    val ESCAPE = EditorAction("escape", KeyCode.ESCAPE)

    val TAB_CLOSE = EditorAction("tab.close", KeyCode.W, control = true, label = "Close Tab")

}

package uk.co.nickthecoder.tickle.editor

import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.gui.ApplicationAction


object EditorActions {

    val nameToActionMap = mutableMapOf<String, ApplicationAction>()

    val RESOURCES_SAVE = EditorAction("resources.save", KeyCode.S, control = true, label = "Save", tooltip = "Save Resources")
    val NEW = EditorAction("new", KeyCode.N, control = true, label = "New", tooltip = "Create a New Resource")
    val RUN = EditorAction("run", KeyCode.R, control = true, label = "Run", tooltip = "Run the game")
    val TEST = EditorAction("test", KeyCode.T, control = true, label = "Test", tooltip = "Test the game")

    val ACCORDION_RESOURCES = EditorAction("accordion.resources", KeyCode.F2)
    val ACCORDION_COSTUME = EditorAction("accordion.costume", KeyCode.F3)
    val ACCORDION_PROPERTIES = EditorAction("accordion.properties", KeyCode.F4)

    val ESCAPE = EditorAction("escape", KeyCode.ESCAPE)
    val DELETE = EditorAction("delete", KeyCode.DELETE)

    val STAMPS = listOf(KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9)
            .mapIndexed { index, keyCode -> EditorAction("stamp$index", keyCode) }

    val TAB_CLOSE = EditorAction("tab.close", KeyCode.W, control = true, label = "Close Tab")

}

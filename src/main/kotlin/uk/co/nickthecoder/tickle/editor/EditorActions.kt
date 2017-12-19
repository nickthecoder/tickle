package uk.co.nickthecoder.tickle.editor

import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.gui.ApplicationAction


object EditorActions {

    val nameToActionMap = mutableMapOf<String, ApplicationAction>()

    val RESOURCES_SAVE = EditorAction("resources.save", KeyCode.S, control = true, label = "Save", tooltip = "Save Resources")
    val NEW = EditorAction("new", KeyCode.N, control = true, label = "New", tooltip = "Create a New Resource")
    val RUN = EditorAction("run", KeyCode.R, control = true, label = "Run", tooltip = "Run the game")
    val TEST = EditorAction("test", KeyCode.T, control = true, label = "Test", tooltip = "Test the game")

    val ACCORDION_ONE = EditorAction("accordion.one", KeyCode.F4)
    val ACCORDION_TWO = EditorAction("accordion.two", KeyCode.F5)
    val ACCORDION_THREE = EditorAction("accordion.three", KeyCode.F6)
    val ACCORDION_FOUR = EditorAction("accordion.four", KeyCode.F7)
    val ACCORDION_FIVE = EditorAction("accordion.five", KeyCode.F8)

    val SHOW_COSTUME_PICKER = EditorAction("show.costumePicker", KeyCode.CONTEXT_MENU)

    val ESCAPE = EditorAction("escape", KeyCode.ESCAPE)
    val DELETE = EditorAction("delete", KeyCode.DELETE)

    val ZOOM_RESET = EditorAction("zoom.reset", KeyCode.DIGIT0, control = true)
    val ZOOM_IN1 = EditorAction("zoom.in", KeyCode.PLUS, control = true)
    val ZOOM_IN2 = EditorAction("zoom.out", KeyCode.EQUALS, control = true)
    val ZOOM_OUT = EditorAction("zoom.out", KeyCode.MINUS, control = true)

    val SNAPS_EDIT = EditorAction("snaps.edit", KeyCode.NUMBER_SIGN, control = true, shift = true, tooltip = "Edit Snapping")

    val SNAP_TO_GRID_TOGGLE = EditorAction("snap.grid.toggle", KeyCode.NUMBER_SIGN, control = true)
    val SNAP_TO_GUIDES_TOGGLE = EditorAction("snap.guides.toggle", KeyCode.G, control = true)
    val SNAP_TO_OTHERS_TOGGLE = EditorAction("snap.others.toggle", KeyCode.O, control = true)
    val SNAP_ROTATION_TOGGLE = EditorAction("snap.rotation.toggle", KeyCode.R, control = true)

    val RESET_ZORDERS = EditorAction("zOrders.reset", KeyCode.Z, control = true, shift = true, label = "Reset All Z-Orders")

    val STAMPS = listOf(KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9)
            .mapIndexed { index, keyCode -> EditorAction("stamp$index", keyCode) }

    val TAB_CLOSE = EditorAction("tab.close", KeyCode.W, control = true, label = "Close Tab")

    val FXCODER = EditorAction("fxcoder", KeyCode.F12, tooltip = "FX Coder")

}

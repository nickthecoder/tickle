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
package uk.co.nickthecoder.tickle.editor

import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.gui.ApplicationAction


object EditorActions {

    val nameToActionMap = mutableMapOf<String, ApplicationAction>()

    val NEW = EditorAction("new", KeyCode.N, control = true, label = "New", tooltip = "Create a New Resource")
    val RUN = EditorAction("run", KeyCode.R, control = true, label = "Run", tooltip = "Run the game")
    val TEST = EditorAction("test", KeyCode.T, control = true, label = "Test", tooltip = "Test the game")
    val RELOAD = EditorAction("reload", KeyCode.F5, label = "Reload", tooltip = "Reload textures, fonts & sounds")

    val ACCORDION_ONE = EditorAction("accordion.one", KeyCode.F4)
    val ACCORDION_TWO = EditorAction("accordion.two", KeyCode.F5)
    val ACCORDION_THREE = EditorAction("accordion.three", KeyCode.F6)
    val ACCORDION_FOUR = EditorAction("accordion.four", KeyCode.F7)
    val ACCORDION_FIVE = EditorAction("accordion.five", KeyCode.F8)

    val SHOW_COSTUME_PICKER = EditorAction("show.costumePicker", KeyCode.CONTEXT_MENU)

    val ESCAPE = EditorAction("escape", KeyCode.ESCAPE)
    val DELETE = EditorAction("delete", KeyCode.DELETE)

    val UNDO = EditorAction("undo", KeyCode.Z, control = true)
    val REDO = EditorAction("redo", KeyCode.Z, control = true, shift = true)

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

    val RELOAD_SCRIPTS = EditorAction("scripts.reload", KeyCode.F5, label = "Reload Scripts", tooltip = "Reload all scripts")

}

package uk.co.nickthecoder.tickle.events

import uk.co.nickthecoder.tickle.graphics.Window

class KeyEvent(
        val window: Window,
        val key: Key,
        val scanCode: Int,
        val state: ButtonState,
        val mods: Int) {

    var consumed: Boolean = false

    fun consume() {
        consumed = true
    }
}

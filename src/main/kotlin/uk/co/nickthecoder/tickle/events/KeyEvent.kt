package uk.co.nickthecoder.tickle.events

import uk.co.nickthecoder.tickle.graphics.Window

class KeyEvent(
        val window : Window,
        val key: Int,
        val scanCode: Int,
        val action: Int,
        val mods: Int)
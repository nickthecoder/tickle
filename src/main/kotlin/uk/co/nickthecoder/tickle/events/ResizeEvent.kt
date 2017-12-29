package uk.co.nickthecoder.tickle.events

import uk.co.nickthecoder.tickle.graphics.Window

data class ResizeEvent(val window: Window, val oldWidth: Int, val oldHeight: Int, val width: Int, val height: Int)
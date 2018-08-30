package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.events.MouseEvent
import java.awt.Desktop
import java.net.URI

class URLButton : Button() {

    @Attribute
    var url = ""

    override fun onClicked(event: MouseEvent) {
        val thread = Thread() {
            Desktop.getDesktop().browse(URI(url))
        }
        thread.isDaemon = true
        thread.start()
    }

}

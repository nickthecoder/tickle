package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.control.TitledPane

interface HasSidePanes {

    fun sidePanes(): Collection<TitledPane>

}

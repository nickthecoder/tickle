package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.Node
import javafx.scene.control.TitledPane

interface HasExtras {

    fun extraSidePanes(): Collection<TitledPane> = emptyList()

    fun extraButtons() : Collection<Node> = emptyList()

}

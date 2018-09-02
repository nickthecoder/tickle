package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.layout.BorderPane

class ClassHelpBox : BorderPane() {

    val tree = ClassTree()

    init {
        center = tree
    }
}

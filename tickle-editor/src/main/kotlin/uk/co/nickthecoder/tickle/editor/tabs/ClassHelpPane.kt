package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView

class ClassHelpBox(webView: WebView) : BorderPane() {

    val tree = APITree(webView)

    init {
        center = tree
    }
}

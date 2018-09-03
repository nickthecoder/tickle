package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView

/**
 * Currently, this only hols an APITree (which lists packages and classes).
 * Later, this will be improved, to allow the tree to be filtered.
 * Either by limiting the classes to the most important ones (thus hiding those classes which are
 * very rarely used), or by searching for a class by name (or a partial name).
 */
class APIBox(webView: WebView) : BorderPane() {

    private val tree = APITree(webView)

    init {
        center = tree
    }
}

package uk.co.nickthecoder.tickle.editor.util

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.tedi.CodeWordBreakIterator
import uk.co.nickthecoder.tedi.TediArea
import uk.co.nickthecoder.tedi.requestFocusOnSceneAvailable
import uk.co.nickthecoder.tedi.ui.RemoveHiddenChildren
import uk.co.nickthecoder.tedi.ui.ReplaceBar
import uk.co.nickthecoder.tedi.ui.SearchBar
import uk.co.nickthecoder.tedi.ui.TextInputControlMatcher
import java.io.File

class CodeEditor {

    val borderPane = BorderPane()

    val tediArea = TediArea()

    val searchAndReplaceBox = VBox()

    val matcher = TextInputControlMatcher(tediArea)

    val searchBar = SearchBar(matcher)
    val replaceBar = ReplaceBar(matcher)

    val dirtyProperty = SimpleBooleanProperty(false)
    var dirty: Boolean
        get() = dirtyProperty.get()
        set(v) {
            dirtyProperty.set(v)
        }

    init {
        with(tediArea) {
            wordIterator = CodeWordBreakIterator()
            displayLineNumbers = true

            textProperty().addListener { _, _, _ ->
                dirty = true
            }
        }

        searchBar.toolBar.styleClass.add(".bottom")
        replaceBar.toolBar.styleClass.add(".bottom")

        RemoveHiddenChildren(searchAndReplaceBox.children)
        searchAndReplaceBox.children.addAll(searchBar.toolBar, replaceBar.toolBar)

        // Hides the search and replace toolbars.
        matcher.inUse = false

        with(borderPane) {
            center = tediArea
            bottom = searchAndReplaceBox
        }

        borderPane.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
    }

    fun load(file: File) {
        tediArea.text = file.readText()
        dirty = false
    }

    fun save(file: File) {
        file.writeText(tediArea.text)
        dirty = false
    }

    fun onKeyPressed(event: KeyEvent) {
        var consume = true

        if (event.isControlDown) {
            when (event.code) {
            // Toggle Line Numbers
                KeyCode.L -> tediArea.displayLineNumbers = !tediArea.displayLineNumbers
            // FIND
                KeyCode.F -> {
                    matcher.inUse = true
                    searchBar.search.requestFocusOnSceneAvailable()
                }
            // Replace (R is already used for "Run")
                KeyCode.H -> {
                    val wasInUse = matcher.inUse
                    replaceBar.toolBar.isVisible = true
                    if (wasInUse) {
                        replaceBar.replacement.requestFocusOnSceneAvailable()
                    }
                }
                else -> consume = false
            }
        } else {
            when (event.code) {
                KeyCode.ESCAPE -> {
                    matcher.inUse = false
                    tediArea.requestFocus()
                }
                else -> consume = false
            }
        }

        if (consume) {
            event.consume()
        }
    }
}

package uk.co.nickthecoder.tickle.editor.util

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.tedi.CodeWordBreakIterator
import uk.co.nickthecoder.tedi.TediArea
import uk.co.nickthecoder.tedi.requestFocusOnSceneAvailable
import uk.co.nickthecoder.tedi.ui.FindBar
import uk.co.nickthecoder.tedi.ui.RemoveHiddenChildren
import uk.co.nickthecoder.tedi.ui.ReplaceBar
import uk.co.nickthecoder.tedi.ui.TextInputControlMatcher
import java.io.File

class CodeEditor {

    val borderPane = BorderPane()

    val tediArea = TediArea()

    private val findAndReplaceBox = VBox()

    private val matcher = TextInputControlMatcher(tediArea)

    private val findBar = FindBar(matcher)
    private val replaceBar = ReplaceBar(matcher)

    init {
        with(tediArea) {
            styleClass.add("code")
            wordIterator = CodeWordBreakIterator()
        }

        findBar.toolBar.styleClass.add(".bottom")
        replaceBar.toolBar.styleClass.add(".bottom")

        RemoveHiddenChildren(findAndReplaceBox.children)
        findAndReplaceBox.children.addAll(findBar.toolBar, replaceBar.toolBar)

        // Hides the find and replace toolbars.
        matcher.inUse = false

        with(borderPane) {
            center = tediArea
            bottom = findAndReplaceBox
        }

        borderPane.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
    }

    fun load(file: File) {
        tediArea.text = file.readText()
    }

    fun save(file: File) {
        file.writeText(tediArea.text)
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
                    findBar.find.requestFocusOnSceneAvailable()
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

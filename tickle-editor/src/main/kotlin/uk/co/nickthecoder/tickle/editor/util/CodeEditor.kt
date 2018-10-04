package uk.co.nickthecoder.tickle.editor.util

import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.tedi.SourceCodeWordIterator
import uk.co.nickthecoder.tedi.TediArea
import uk.co.nickthecoder.tedi.requestFocusOnSceneAvailable
import uk.co.nickthecoder.tedi.syntax.GroovySyntax
import uk.co.nickthecoder.tedi.syntax.HighlightMatchedPairs
import uk.co.nickthecoder.tedi.ui.FindBar
import uk.co.nickthecoder.tedi.ui.RemoveHiddenChildren
import uk.co.nickthecoder.tedi.ui.ReplaceBar
import uk.co.nickthecoder.tedi.ui.TextInputControlMatcher
import uk.co.nickthecoder.tickle.scripts.ScriptException
import java.io.File
import java.util.regex.Pattern

class CodeEditor {

    val borderPane = BorderPane()

    val tediArea = TediArea()

    private val error = Label("")

    private val findAndReplaceBox = VBox()

    private val matcher = TextInputControlMatcher(tediArea)

    private val findBar = FindBar(matcher)
    private val replaceBar = ReplaceBar(matcher)

    init {
        with(tediArea) {
            styleClass.add("code")
            wordIterator = SourceCodeWordIterator()

            GroovySyntax.instance.attach(tediArea)
            HighlightMatchedPairs(tediArea)
        }

        findBar.toolBar.styleClass.add(".bottom")
        replaceBar.toolBar.styleClass.add(".bottom")

        RemoveHiddenChildren(findAndReplaceBox.children)
        findAndReplaceBox.children.addAll(error, findBar.toolBar, replaceBar.toolBar)

        // Hides the find and replace toolbars.
        matcher.inUse = false

        with(borderPane) {
            center = tediArea
            bottom = findAndReplaceBox
        }

        error.isVisible = false
        error.styleClass.add("code-error")

        borderPane.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
        tediArea.addEventFilter(MouseEvent.MOUSE_PRESSED) { onMousePressedRelease(it) }
        tediArea.addEventFilter(MouseEvent.MOUSE_RELEASED) { onMousePressedRelease(it) }
    }

    fun highlightError(e: ScriptException) {
        val line = e.line
        if (line != null) {
            error.onMouseClicked = EventHandler {
                positionCaret(line, e.column)
                tediArea.requestFocus()
            }
            positionCaret(line, e.column)
        } else {
            error.onMouseClicked = null
        }
        error.text = e.message
        error.isVisible = true
        tediArea.requestFocus()
    }

    fun hideError() {
        error.isVisible = false
    }

    fun positionCaret(line: Int, column: Int?) {
        if (column == null) {
            tediArea.positionCaret(tediArea.positionOfLine(line - 1))
        } else {
            tediArea.positionCaret(tediArea.positionOfLine(line - 1, column - 1))
        }
    }

    fun load(file: File) {
        tediArea.text = file.readText()
    }

    fun save(file: File) {
        file.writeText(tediArea.text)
    }

    fun onMousePressedRelease(event: MouseEvent) {
        if (event.isPopupTrigger) {
            tediArea.contextMenu = buildContextMenu(event)
            tediArea.contextMenu?.let {
                tediArea.contextMenu.show(tediArea, event.screenX, event.screenY)
                event.consume()
            }
        }
    }

    fun buildContextMenu(event: MouseEvent): ContextMenu? {
        val menu = ContextMenu()
        var addSeparator = false
        fun addItem(item: MenuItem) {
            if (addSeparator && menu.items.isNotEmpty()) {
                menu.items.add(SeparatorMenuItem())
            }
            menu.items.add(item)
        }

        val word = wordAt(event)
        if (word != null) {
            val classNames = findClasses(word)
            classNames.filter { !isImported(it) }.forEach { className ->
                val item2 = MenuItem("Import $className")
                item2.onAction = EventHandler { addImport(className) }
                addItem(item2)

                val pack = packageName(className)
                val item1 = MenuItem("Import $pack.*")
                item1.onAction = EventHandler { addImport("$pack.*") }
                addItem(item1)
            }
            if (classNames.size > 1) addSeparator = true
        }
        return if (menu.items.isEmpty()) null else menu
    }

    fun isImported(className: String): Boolean {
        val im = importPattern.matcher(tediArea.text)
        while (im.find()) {
            val imp = im.group("IMPORT")
            if (imp == className || imp == "${packageName(className)}.*") {
                return true
            }
        }
        return false
    }

    fun packageName(className: String) = className.substring(0, className.lastIndexOf('.'))

    fun findClasses(word: String): List<String> {

        return ClassMetaData.simpleClassNameToNames[word] ?: emptyList()
    }

    fun addImport(imp: String) {
        tediArea.insertText(importPosition(), "import $imp\n")
    }

    fun importPosition(): Int {
        val text = tediArea.text
        var pos = 0

        val pm = packagePattern.matcher(text)
        while (pm.find()) {
            pos = tediArea.positionOfLine(1 + tediArea.lineForPosition(pm.start()))
        }

        val im = importPattern.matcher(text)
        while (im.find()) {
            pos = tediArea.positionOfLine(1 + tediArea.lineForPosition(im.start()))
        }
        return pos
    }

    fun wordAt(event: MouseEvent): String? {
        val pos = tediArea.positionForPoint(event.x, event.y)
        val (line, column) = tediArea.lineColumnForPosition(pos)
        val lineText = tediArea.paragraphs[line].text
        // Find the word at the given point
        val matcher = wordPattern.matcher(lineText)

        while (matcher.find()) {
            if (matcher.start() <= column && matcher.end() >= column) {
                return lineText.substring(matcher.start(), matcher.end())
            }
        }
        return null
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

    companion object {
        val wordPattern: Pattern = Pattern.compile("\\b\\w+?\\b")
        val packagePattern: Pattern = Pattern.compile("^\\spackage", Pattern.MULTILINE)
        val importPattern: Pattern = Pattern.compile("^\\s*import\\s(?<IMPORT>.*)\\s", Pattern.MULTILINE)
    }
}

package uk.co.nickthecoder.tickle.editor.util

import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class CodeTextArea : TextArea() {

    val tabSize = 4

    init {
        this.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if (event.code == KeyCode.TAB) {
                if (event.isShiftDown) {
                    unIndent()
                } else {
                    indent()
                }
                event.consume()
            }
        }
    }

    /**
     * Returns the position of the caret from the beginning of the line.
     * e.g. if it is at the far left of a line, then return 0
     */
    private fun column(textPosition: Int): Int {
        var pos = textPosition
        while (pos > 0 && text[pos - 1] != '\n') pos--
        return caretPosition - pos
    }

    private fun startOfLine(textPosition: Int): Int {
        var pos = textPosition
        while (pos > 0 && text[pos - 1] != '\n') pos--
        return pos
    }

    private fun endOfLine(textPosition: Int): Int {
        var pos = textPosition
        while (pos < text.length && text[pos] != '\n') pos++
        return pos
    }

    fun indent() {
        if (selectedText.isEmpty()) {
            val column = column(caretPosition)
            val amount = tabSize - column.rem(tabSize)
            insertText(caretPosition, " ".repeat(amount))
        } else {
            // Indent a block of code, which may be multiple lines
            val start = startOfLine(selection.start)
            val end = endOfLine(selection.end)
            val lines = text.substring(start, end).split('\n')
            val replacement = lines.map { " ".repeat(tabSize) + it }.joinToString(separator = "\n")
            deleteText(start, end)
            insertText(start, replacement)
            selectRange(start, start + replacement.length)
        }
    }

    fun unIndent() {
        if (selectedText.isEmpty()) {
            val start = caretPosition - column(caretPosition)
            var end = start
            for (i in 0..tabSize - 1) {
                if (start + i > text.length) {
                    break
                }
                if (text[start + i] == ' ') {
                    end++
                } else {
                    break
                }
            }
            deleteText(start, end)
        } else {
            // Un-Indent a block of code, which may be multiple lines
            val start = startOfLine(selection.start)
            val end = endOfLine(selection.end)
            val lines = text.substring(start, end).split('\n')
            val spaces = " ".repeat(tabSize)
            for (line in lines) {
                if (line.isNotBlank() && !line.startsWith(spaces)) {
                    return
                }
            }
            val replacement = lines.map { it.substring(tabSize) }.joinToString(separator = "\n")
            deleteText(start, end)
            insertText(start, replacement)
            selectRange(start, start + replacement.length)
        }
    }

}

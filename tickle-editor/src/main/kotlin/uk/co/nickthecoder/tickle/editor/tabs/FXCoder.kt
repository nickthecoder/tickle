/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.editor.tabs

import groovy.lang.GroovyRuntimeException
import groovy.util.Eval
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.util.CodeEditor
import uk.co.nickthecoder.tickle.editor.util.toImageView
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.groovy.GroovyLanguage
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import javax.imageio.ImageIO


class FXCoderTab : EditorTab("FX Coder", FXCoder()) {

    init {
        content = (data as FXCoder).borderPane
    }
}

class FXCoder {

    internal val borderPane = BorderPane()

    private val vSplitPane = SplitPane()

    private val hSplitPane = SplitPane()

    private var guiResults: Node? = null

    private val guiResultsScroll = ScrollPane()

    private val messageArea = TextArea()

    private val codeEditor = CodeEditor()

    private val buttons = FlowPane()

    private val saveButton = Button("Save")

    private val openButton = Button("Open")

    private val runButton = Button("Run")

    private val saveImageButton = Button("Save Image")

    private var file: File? = null

    init {

        borderPane.center = vSplitPane
        borderPane.bottom = buttons

        vSplitPane.items.addAll(codeEditor.borderPane)
        vSplitPane.orientation = Orientation.VERTICAL

        hSplitPane.items.addAll(messageArea)

        messageArea.prefRowCount = 3
        messageArea.isEditable = false

        buttons.styleClass.add("buttons")
        buttons.children.addAll(saveImageButton, saveButton, openButton, runButton)
        saveImageButton.isVisible = false

        runButton.onAction = EventHandler { run() }
        openButton.onAction = EventHandler { open() }
        saveButton.onAction = EventHandler { save() }
        saveImageButton.onAction = EventHandler { saveImage() }

        codeEditor.tediArea.text = defaultCode
    }

    private fun createFileChooser(): FileChooser = FileChooser().apply {
        extensionFilters.add(FileChooser.ExtensionFilter("Groovy Source Code", "*.groovy"))
    }

    fun open() {
        val openFile = createFileChooser().showOpenDialog(MainWindow.instance.stage)

        if (openFile != null && openFile.exists()) {
            codeEditor.tediArea.text = openFile.readText()
            file = openFile
        }
    }

    private fun save() {
        if (file == null) {
            file = createFileChooser().showSaveDialog(MainWindow.instance.stage)
        }
        file?.writeText(codeEditor.tediArea.text)
    }

    private fun saveImage() {
        val results = guiResults

        if (results is Canvas || results is ImageView) {

            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("png files (*.png)", "*.png"))
            val file = fileChooser.showSaveDialog(MainWindow.instance.stage)

            if (file != null) {
                if (results is Canvas) {
                    saveCanvas(results, file)
                } else if (results is ImageView) {
                    saveImage(results.image, file)
                }
            }
        }
    }

    private fun run() {
        saveImageButton.isVisible = false
        messageArea.text = "Running..."
        runButton.isDisable = true
        borderPane.requestLayout()
        hSplitPane.items.remove(guiResultsScroll)
        codeEditor.hideError()

        runScript()
    }

    private fun runScript() {

        try {

            val result = Eval.me(codeEditor.tediArea.text)

            guiResults = when (result) {
                is Node -> result
                is Image -> ImageView(result)
                is Pose -> result.toImageView(false)
                is Texture -> result.toImageView(false)
                else -> null
            }

            if (guiResults != null) {
                guiResultsScroll.content = guiResults
                hSplitPane.items.add(guiResultsScroll)
            }
            messageArea.text = result?.toString() ?: "No results returned"
            saveImageButton.isVisible = guiResults is Canvas || guiResults is ImageView


        } catch (e: Exception) {

            if (e is GroovyRuntimeException) {
                codeEditor.highlightError(GroovyLanguage.convertException(e))
                messageArea.text = "Error"
            } else {
                val stringWriter = StringWriter()
                e.printStackTrace(PrintWriter(stringWriter))
                messageArea.text = stringWriter.toString()
            }

        } finally {
            runButton.isDisable = false

            if (vSplitPane.items.size < 2) {
                vSplitPane.items.add(hSplitPane)
                vSplitPane.setDividerPosition(0, 0.9)
            }
        }
    }

    companion object {

        @JvmStatic
        fun saveCanvas(canvas: Canvas, file: File) {
            val writableImage = WritableImage(canvas.width.toInt(), canvas.height.toInt())
            val snapParams = SnapshotParameters().also { it.fill = Color.TRANSPARENT }
            canvas.snapshot(snapParams, writableImage)
            val renderedImage = SwingFXUtils.fromFXImage(writableImage, null)
            ImageIO.write(renderedImage, "png", file)
        }

        @JvmStatic
        fun saveImage(image: Image, file: File) {
            val renderedImage = SwingFXUtils.fromFXImage(image, null)
            ImageIO.write(renderedImage, "png", file)
        }

        val defaultCode = """// FXCoder lets you run groovy scripts.
// This can be useful for generating graphics, or automating processes in the Tickle editor.
// Here's an example script as an example...

import uk.co.nickthecoder.tickle.editor.tabs.FXCoder
import javafx.scene.canvas.Canvas
import javafx.scene.paint.*

def w = 50.0
def h = 30.0

// Note: scripts are run in the JavaFX thread, which will cause the GUI
// to freeze if your script takes a long time.
// You can consider using Threads, but remember that GUI operations,
// (including OpenGL operations) must be on the JavaFX thread.

def canvas = new Canvas(w,h)
def context = canvas.graphicsContext2D

context.fill = Color.WHEAT
context.fillRect(0.0, 0.0, w,h)

// By returning a Canvas or an Image, a "Save Image" button will appear.
// Alternatively, you could save it from within the script.
//     FXCoder.saveCanvas( canvas, file )
//     FXCoder.saveImage( image, file )
canvas
"""

    }

}

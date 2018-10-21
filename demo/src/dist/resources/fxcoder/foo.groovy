// FXCoder lets you run groovy scripts.
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

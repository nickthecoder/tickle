package uk.co.nickthecoder.tickle.sandbox

import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWTAndJavaFX
import java.awt.Font

/**
 * Creates a FontTexture, and draws it in a window.
 * The whole texture is drawn semi-transparent, and some specific lines of text are draw full opaque over the top.
 */
class JavaFXFontSandbox() : Sandbox() {

    val fontName = Font.SANS_SERIF
    val fontSize = 22

    val font = FontTextureFactoryViaAWTAndJavaFX(
            java.awt.Font(fontName, Font.PLAIN, 22),
            javafx.scene.text.Font(fontName, fontSize.toDouble())).create()

    val pose = Pose(font.texture)

    var x = 10.0
    var y = 10.0

    override fun tick() {
        renderer.beginView()
        renderer.clear()

        pose.draw(renderer, 100.0, 200.0, Color.SEMI_TRANSPARENT_WHITE)

        font.draw(renderer, "Hello World. 3°", 20.0, 100.0)
        font.draw(renderer, "qyj (can we see the descenders?)", 0.0, 60.0)
        font.draw(renderer, "qyj (descenders should be hidden)", 0.0, 0.0)

        renderer.endView()
        window.swap()
    }
}

fun main(args: Array<String>) {
    JavaFXFontSandbox().loop()
}

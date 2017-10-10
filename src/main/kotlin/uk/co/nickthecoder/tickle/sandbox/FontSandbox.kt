package uk.co.nickthecoder.tickle.sandbox

import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.FontTextureFactory
import java.awt.Font

/**
 * Creates a FontTexture, and draws it in a window.
 * The whole texture is drawn semi-transparent, and some specific lines of text are draw full opaque over the top.
 */
class FontSandbox() : Sandbox() {

    val font = FontTextureFactory(Font(Font.SANS_SERIF, Font.PLAIN, 22), true).create()

    val pose = Pose(font.texture)

    var x = 10.0
    var y = 10.0

    override fun tick() {
        renderer.beginView()
        renderer.clear()

        pose.draw(renderer, 100.0, 200.0, Color.SEMI_TRANSPARENT)

        font.draw(renderer, "Hello World. 3°", 20.0, 100.0)
        font.draw(renderer, "qyj (can we see the descenders?)", 0.0, 60.0)
        font.draw(renderer, "qyj (descenders should be hidden)", 0.0, 0.0)

        renderer.endView()
        window.swap()
    }
}

fun main(args: Array<String>) {
    FontSandbox().loop()
}

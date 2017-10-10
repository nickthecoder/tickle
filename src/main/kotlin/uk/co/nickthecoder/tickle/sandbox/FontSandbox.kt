package uk.co.nickthecoder.tickle.sandbox

import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWT
import java.awt.Font

/**
 * Creates a FontTexture, and draws it in a window.
 * The whole texture is drawn semi-transparent, and some specific lines of text are draw full opaque over the top.
 */
class FontSandbox() : Sandbox() {

    val font = FontTextureFactoryViaAWT(Font(Font.SANS_SERIF, Font.PLAIN, 22)).create()

    val pose = Pose(font.texture)

    var x = 10.0
    var y = 10.0

    init {
        renderer.clearColor(Color.BLUE)
    }

    override fun tick() {
        renderer.beginView()
        renderer.clear()

        pose.draw(renderer, 100.0, 200.0, Color.SEMI_TRANSPARENT_WHITE)

        val text = ".oO° Hello °Oo."
        font.draw(renderer, "qyj (can we see the descenders?)", 0.0, 60.0)
        font.draw(renderer, "qyj (descenders should be hidden)", 0.0, 0.0)

        font.draw(renderer, "$text - Plain", 20.0, 100.0)
        font.drawOutlined(renderer, "$text - Semi-Transparent Outline", 20.0, 130.0, outline = Color.SEMI_TRANSPARENT_BLACK)
        font.drawOutlined(renderer, "$text - Outlined", 20.0, 160.0, outline = Color.SEMI_TRANSPARENT_BLACK)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 3, alpha 0.3", 20.0, 190.0, outline = Color(0.0f, 0.0f, 0.0f, 0.3f), thickness = 3)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 6, alpha 0.05", 20.0, 220.0, outline = Color(0.0f, 0.0f, 0.0f, 0.05f), thickness = 6)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 6, alpha 0.025", 20.0, 250.0, outline = Color(0.0f, 0.0f, 0.0f, 0.025f), thickness = 6)

        renderer.endView()
        window.swap()
    }
}

fun main(args: Array<String>) {
    FontSandbox().loop()
}

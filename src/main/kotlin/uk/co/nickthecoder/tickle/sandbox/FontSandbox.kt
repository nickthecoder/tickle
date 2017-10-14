package uk.co.nickthecoder.tickle.sandbox

import uk.co.nickthecoder.tickle.FontResource
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.HAlignment
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.graphics.VAlignment
import java.awt.Font

/**
 * Creates a FontTexture, and draws it in a window.
 * The whole texture is drawn semi-transparent, and some specific lines of text are draw full opaque over the top.
 */
class FontSandbox : Sandbox(height = 700) {

    val xPadding = 1
    val yPadding = 1

    val fontResource = FontResource(Font.SANS_SERIF, FontResource.FontStyle.PLAIN, 22.0, xPadding, yPadding)

    val font = fontResource.fontTexture

    val pose = Pose(font.glyphs.values.first().pose.texture)

    var x = 10.0
    var y = 10.0

    init {
        renderer.clearColor(Color.BLUE)
    }

    override fun tick() {
        renderer.beginView()
        renderer.clear()

        pose.draw(renderer, 250.0, 400.0, Color.SEMI_TRANSPARENT_WHITE)

        val text = ".oO° Hello Worldy °Oo."

        font.draw(renderer, "$text - Plain", 20.0, 100.0)

        font.drawOutlined(renderer, "$text - Semi-Transparent Outline", 20.0, 130.0, outline = Color.SEMI_TRANSPARENT_BLACK)
        font.drawOutlined(renderer, "$text - Outlined", 20.0, 160.0, outline = Color.SEMI_TRANSPARENT_BLACK)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 3, alpha 0.3", 20.0, 190.0, outline = Color(0.0f, 0.0f, 0.0f, 0.3f), thickness = 3)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 6, alpha 0.05", 20.0, 220.0, outline = Color(0.0f, 0.0f, 0.0f, 0.05f), thickness = 6)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 6, alpha 0.025", 20.0, 250.0, outline = Color(0.0f, 0.0f, 0.0f, 0.025f), thickness = 6)

        val topLeft = TextStyle(fontResource, HAlignment.LEFT, VAlignment.TOP, Color.WHITE)
        val topRight = TextStyle(fontResource, HAlignment.RIGHT, VAlignment.TOP, Color.WHITE)
        val bottomLeft = TextStyle(fontResource, HAlignment.LEFT, VAlignment.BOTTOM, Color.WHITE)
        val bottomRight = TextStyle(fontResource, HAlignment.RIGHT, VAlignment.BOTTOM, Color.WHITE)
        val baselineCenter = TextStyle(fontResource, HAlignment.CENTER, VAlignment.BASELINE, Color.WHITE)
        val center = TextStyle(fontResource, HAlignment.CENTER, VAlignment.CENTER, Color.WHITE)

        topLeft.draw(renderer, "Top\nLeft", 0.0, window.height.toDouble())
        topRight.draw(renderer, "Top\nRight", window.width.toDouble(), window.height.toDouble())
        bottomLeft.draw(renderer, "Bottom\nLeft", 0.0, 0.0)
        bottomRight.draw(renderer, "Bottom\nRight", window.width.toDouble(), 0.0)
        baselineCenter.draw(renderer, "Baseline\nCenter\njqy", window.width.toDouble() / 2, 0.0)
        center.draw(renderer, "Center\nCenter\njqy", window.width.toDouble() / 2, window.height.toDouble() / 2)

        renderer.endView()
        window.swap()
    }
}

fun main(args: Array<String>) {
    FontSandbox().loop()
}

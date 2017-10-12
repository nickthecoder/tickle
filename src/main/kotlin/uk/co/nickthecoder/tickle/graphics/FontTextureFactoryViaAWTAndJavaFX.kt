package uk.co.nickthecoder.tickle.graphics

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.util.YDownRect

/**
 * JavaFX doesn't have a public API for font metrics, but AWT doesn't allow fonts to be stroked (only filled).
 * This class attempts to use both in combination. It uses AWT's font metrics and then uses JavaFX to render
 * to a canvas.
 *
 * I've abandoned this code before finishing it, but may return to it later. Hmm.
 */
class FontTextureFactoryViaAWTAndJavaFX(awtFont: java.awt.Font, val javaFXFont: Font)
    : FontTextureFactoryViaAWT(awtFont) {


    override fun create(): FontTexture {
        // IF no ranges were specified, use the default range of 32..255
        if (glyphDataMap.isEmpty()) {
            range()
        }

        val canvas = Canvas(requiredWidth.toDouble(), requiredHeight.toDouble())
        val g = canvas.graphicsContext2D

        g.font = javaFXFont
        g.fill = Color.WHITE

        glyphDataMap.forEach { c, glyphData ->
            // println("Drawing glyph $c ${glyphData.x},${(glyphData.y - glyphData.bounds.y).toInt()}")
            g.fillText(c.toString(), glyphData.x.toDouble(), glyphData.y - (glyphData.bounds.height + glyphData.bounds.y))
        }

        val image = WritableImage(requiredWidth, requiredHeight)
        canvas.snapshot(SnapshotParameters(), image)
        val reader = image.pixelReader

        /* Put pixel data into a ByteBuffer, ensuring it is in RGBA order */
        val buffer = MemoryUtil.memAlloc(requiredWidth * requiredHeight * 4)
        for (y in 0..requiredHeight - 1) {
            for (x in 0..requiredWidth - 1) {
                /* Pixel format is : 0xAARRGGBB */
                val pixel = reader.getArgb(x, y)
                buffer.put((pixel shr 16 and 0xFF).toByte())
                buffer.put((pixel shr 8 and 0xFF).toByte())
                buffer.put((pixel and 0xFF).toByte())
                buffer.put((pixel shr 24 and 0xFF).toByte())
            }
        }

        buffer.flip()

        val texture = Texture(requiredWidth, requiredHeight, GL11.GL_RGBA, buffer)
        MemoryUtil.memFree(buffer)

        val glyphs = mutableMapOf<Char, Glyph>()
        glyphDataMap.forEach { c, glyphData ->
            val rect = YDownRect(glyphData.x, glyphData.y, glyphData.x + glyphData.bounds.width.toInt(), glyphData.y - glyphData.bounds.height.toInt())
            val pose = Pose(texture, rect)
            pose.offsetX = -glyphData.bounds.x
            pose.offsetY = glyphData.bounds.y
            glyphs[c] = Glyph(pose, glyphData.advance)
            // println("Created Glyph $c : ${rect} offset= ${pose.offsetX},${pose.offsetY} advance=${glyphData.advance}")
        }

        return FontTexture(texture, glyphs, metrics.height.toDouble())
    }

}

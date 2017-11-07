package uk.co.nickthecoder.tickle.editor.util

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.io.File
import javax.imageio.ImageIO

private data class PixelWeight(val dx: Int, val dy: Int, val weight: Double)

private val orthWeight = 1.0
private val diagWeight = 0.5

private val weights = listOf(
        PixelWeight(-1, 0, orthWeight),
        PixelWeight(1, 0, orthWeight),
        PixelWeight(0, -1, orthWeight),
        PixelWeight(0, 1, orthWeight),

        PixelWeight(-1, -1, diagWeight),
        PixelWeight(1, -1, diagWeight),
        PixelWeight(-1, 1, diagWeight),
        PixelWeight(1, 1, diagWeight)
)

/**
 * Changes the RGB values for transparent pixels, so that they match the RGB values of neighbouring pixels.
 * A transparent pixel can have any RGB values, and still look the same (transparent!). However, due to the
 * way that OpenGL renders images, the RGB values of these transparent pixel can "leak" into opaque pixels.
 * This usually manifests as a white border around objects (especially when rotated). It is usually white,
 * because many graphics programs set the RGB values to white for transparent pixels.
 * This function fixes the problem by changing the RGB values of transparent pixels, so that they are a weighted
 * average of the neighbouring RGB values.
 */
fun colorTransparentPixels(file: File) {

    val image = Image(file.inputStream())
    val width = image.width.toInt()
    val height = image.height.toInt()
    val reader = image.pixelReader
    val dest = WritableImage(width, height)
    val writer = dest.pixelWriter

    fun averageColor(ox: Int, oy: Int): Color {
        var rsum = 0.0
        var gsum = 0.0
        var bsum = 0.0
        var divider = 0.0

        weights.forEach { (dx, dy, weight) ->
            val x = ox + dx
            val y = oy + dy
            if (x >= 0 && x < width && y >= 0 && y < height) {
                val neighbour = reader.getColor(x, y)
                val scale = weight * neighbour.opacity
                rsum += neighbour.red * scale
                gsum += neighbour.green * scale
                bsum += neighbour.blue * scale
                divider += scale
            }
        }

        if (rsum > 0) {
            return Color(rsum / divider, gsum / divider, bsum / divider, 0.0)
        } else {
            return Color.TRANSPARENT
        }
    }

    for (y in 0..height - 1) {
        for (x in 0..width - 1) {
            val sourceColor = reader.getColor(x, y)
            if (sourceColor.opacity == 0.0) {
                // Transparent, so use the rgb values from surrounding pixels
                writer.setColor(x, y, averageColor(x, y))
            } else {
                // At least slightly opaque, therefore just copy the color
                writer.setColor(x, y, sourceColor)
            }
        }
    }
    val bufferedImage = SwingFXUtils.fromFXImage(image, null)
    ImageIO.write(bufferedImage, "png", file)
}

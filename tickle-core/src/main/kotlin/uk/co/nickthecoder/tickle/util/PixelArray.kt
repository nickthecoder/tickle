package uk.co.nickthecoder.tickle.util

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import org.lwjgl.opengl.GL11.GL_RGBA
import uk.co.nickthecoder.tickle.graphics.Texture
import java.nio.ByteBuffer

/**
 * For manipulating images as a byte array.
 * The order of the channels in the byte array is RGBA.
 * When reading/writing pixels as ints, then the MSByte is the alpha channel i.e.
 * the int is 0xAABBGGRR
 */
class PixelArray(val width: Int, val height: Int, val array: ByteArray) {

    constructor(width: Int, height: Int) : this(width, height, ByteArray(width * height * 4))

    constructor(texture: Texture) : this(texture.width, texture.height, texture.read(true))

    fun redAt(x: Int, y: Int) = array[(y * width + x) * 4].toInt() and 0xff
    fun greenAt(x: Int, y: Int) = array[(y * width + x) * 4 + 1].toInt() and 0xff
    fun blueAt(x: Int, y: Int) = array[(y * width + x) * 4 + 2].toInt() and 0xff
    fun alphaAt(x: Int, y: Int) = array[(y * width + x) * 4 + 3].toInt() and 0xff

    /**
     */
    fun pixelAt(x: Int, y: Int): Int {
        val offset = (y * width + x) * 4
        return array[offset].toInt() and 0xff or
                (array[offset + 1].toInt() and 0xff).shl(8) or
                (array[offset + 2].toInt() and 0xff).shl(16) or
                (array[offset + 3].toInt() and 0xff).shl(24)
    }

    /**
     * Gets a single pixel's RGB channels as an int
     */
    fun colorAt(x: Int, y: Int): Int {
        val offset = (y * width + x) * 4
        return (array[offset].toInt() and 0xff) or
                (array[offset + 1].toInt() and 0xff).shl(8) or
                (array[offset + 2].toInt() and 0xff).shl(16)
    }

    /**
     * Compatible with PixerWriter.setArgb.
     */
    fun argbColorAt(x: Int, y: Int): Int {
        val offset = (y * width + x) * 4
        return (array[offset + 2].toInt() and 0xff) or // Blue
                (array[offset + 1].toInt() and 0xff).shl(8) or // Green
                (array[offset].toInt() and 0xff).shl(16) or // Red
                (array[offset + 3].toInt() and 0xff).shl(24) // alpha
    }

    fun setPixel(x: Int, y: Int, pixel: Int) {
        val offset = (y * width + x) * 4
        array[offset] = red(pixel).toByte()
        array[offset + 1] = green(pixel).toByte()
        array[offset + 2] = blue(pixel).toByte()
        array[offset + 3] = alpha(pixel).toByte()
    }

    /**
     * Sets the RGB channels, leaving the alpha untouched.
     */
    fun setColor(x: Int, y: Int, pixel: Int) {
        val offset = (y * width + x) * 4
        array[offset] = (pixel and 0xff).toByte()
        array[offset + 1] = (pixel.shr(8) and 0xff).toByte()
        array[offset + 2] = (pixel.shr(16) and 0xff).toByte()
    }

    fun setAlpha(x: Int, y: Int, alpha: Int) {
        array[(y * width + x) * 4 + 3] = (alpha and 0xff).toByte()
    }

    fun toBuffer(flip: Boolean): ByteBuffer {
        println("Creating buffer size ${array.size} (same as ${width * height * 4}) for $width x $height")
        val buffer = ByteBuffer.allocateDirect(array.size)
        if (flip) {
            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    for (c in 0..3) {
                        buffer.put(array[(y * width + x) * 4 + c])
                    }
                }
            }
        } else {
            buffer.put(array)
        }
        buffer.position(0)
        return buffer
    }

    fun toTexture() = Texture(width, height, GL_RGBA, toBuffer(true))

    fun toImage(): Image {
        val image = WritableImage(width, height)
        val writer = image.pixelWriter
        for (y in 0 until height) {
            for (x in 0 until width) {
                writer.setArgb(x, y, argbColorAt(x, y))
            }
        }
        return image
    }

    fun dumpAlpha() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (alphaAt(x, y) and 128 == 0) {
                    print("#")
                } else {
                    print(" ")
                }
            }
            println()
        }
    }

    companion object {
        fun alpha(pixel: Int) = pixel.shr(24) and 0xff
        fun blue(pixel: Int) = pixel.shr(16) and 0xff
        fun green(pixel: Int) = pixel.shr(8) and 0xff
        fun red(pixel: Int) = pixel and 0xff

        fun pixel(red: Int, green: Int, blue: Int, alpha: Int): Int {
            return red + green.shl(8) + blue.shl(16) + alpha.shl(24)
        }

        fun color(red: Int, green: Int, blue: Int): Int {
            return red + green.shl(8) + blue.shl(16)
        }
    }
}

package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer


class Texture(val width: Int, val height: Int, pixelFormat: Int, buffer: ByteBuffer, val file: File? = null) {

    private var handle: Int? = glGenTextures()

    init {
        glBindTexture(GL_TEXTURE_2D, handle!!)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, buffer)
    }

    private var pixels: ByteArray? = null

    private fun ensurePixelData() {
        pixels = ByteArray(width * height * 4)
        val buffer = ByteBuffer.allocateDirect(width * height * 4)
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        buffer.get(pixels)
    }

    fun alphaAt(x: Int, y: Int): Int {
        ensurePixelData()
        if (x < 0 || x >= height || y < 0 || y >= height) return -1
        // Hmm, it seems my texture is upside down. so i do "height-y-1" rather than "y"
        val index = (x + (height-y-1) * width) * 4 + 3
        return pixels?.get(index)?.toInt()?.and(0xFF) ?: -2
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, handle!!)
        boundHandle = handle
    }

    fun unbind() {
        if (boundHandle == handle) {
            glBindTexture(GL_TEXTURE_2D, 0)
            boundHandle = null
        }
    }

    fun cleanUp() {
        handle?.let {
            unbind()
            glDeleteTextures(it)
            handle = null
        }
    }

    fun finalize() {
        cleanUp()
    }

    override fun toString() = "Texture $width x $height"

    companion object {

        val OUTSIDE = Color(0f, 0f, 0f, 0f)

        private var boundHandle: Int? = null

        fun create(file: File): Texture {

            MemoryStack.stackPush().use { stack ->

                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                val channels = stack.mallocInt(1)

                STBImage.stbi_set_flip_vertically_on_load(true)
                val buffer = stbi_load(file.path, width, height, channels, 4)
                buffer ?: throw IOException("Failed to load texture from ${file.absoluteFile}")

                return Texture(width.get(), height.get(), GL_RGBA, buffer, file)
            }
        }

    }

}

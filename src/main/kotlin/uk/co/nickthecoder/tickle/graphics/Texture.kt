package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

class Texture(val width: Int, val height: Int, pixelFormat: Int, buffer: ByteBuffer) {

    private val handle: Int = glGenTextures()

    init {
        glBindTexture(GL_TEXTURE_2D, handle)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, buffer)
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, handle)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun cleanUp() {
        glDeleteTextures(handle)
    }

    override fun toString() = "Texture $width x $height"

    companion object {

        fun createTexture(file: File): Texture {

            MemoryStack.stackPush().use { stack ->

                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                val channels = stack.mallocInt(1)

                STBImage.stbi_set_flip_vertically_on_load(true)
                val buffer = stbi_load(file.path, width, height, channels, 4)
                if (buffer == null) {
                    throw IOException("Failed to load texture from ${file.absoluteFile}")
                }
                return Texture(width.get(), height.get(), GL_RGBA, buffer)
            }
        }

    }

}

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

        private var boundHandle: Int? = null

        fun createTexture(file: File): Texture {

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

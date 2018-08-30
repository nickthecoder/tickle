package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer


class Texture(val width: Int, val height: Int, val pixelFormat: Int, buffer: ByteBuffer?, val file: File? = null)

    : Deletable, Renamable {

    var privateHandle: Int = glGenTextures()

    val handle: Int
        get() = privateHandle

    init {
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, buffer)
    }

    fun reload() {
        file?.let {
            val loadedImage = load(it)
            unbind()
            glDeleteTextures(handle)
            privateHandle = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, handle)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, loadedImage.buffer)
        }
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, handle)
        boundHandle = handle
    }

    fun unbind() {
        if (boundHandle == handle) {
            glBindTexture(GL_TEXTURE_2D, 0)
            boundHandle = null
        }
    }

    fun cleanUp() {
        unbind()
        glDeleteTextures(handle)
    }

    /**
     * Can only delete if there are no Poses using this texture.
     */
    override fun usedBy(): Any? {
        return Resources.instance.poses.items().values.firstOrNull() { it.texture == this }
    }

    override fun delete() {
        Resources.instance.textures.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.textures.rename(this, newName)
    }

    /**
     * Transfers the texture from the GPU back to main memory (which is SLOW).
     * The result is a ByteArray or size width * height * 4, and the format is RGBA.
     * i.e. to get the alpha value at x,y :
     *
     *     read()[ (y * height + x) * 4 + 3 ]
     *
     * and then deal with the annoyance of java's lack of unsigned bytes. Grr :
     *
     *     .toInt() & 0xFF
     */
    fun read(): ByteArray {
        bind()
        val pixels = ByteArray(width * height * 4)
        val buffer = ByteBuffer.allocateDirect(pixels.size)
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        buffer.get(pixels)
        return pixels
    }

    /**
     * Ascii-art style image of the texture.
     * This is used for debugging only. It dumps the alpha channel of the texture, showing values in the range 0..ff
     * I used it a lot when debugging the PixelOverlap code
     */
    fun dumpAlpha() {
        println("Texture. Dumping alpha channel")
        val pixels = read()
        for (y in height - 1 downTo 0) {
            for (x in 0..width - 1) {
                val alpha = pixels[(x + (y * width)) * 4 + 3]
                print(String.format("%02x", alpha.toInt() and 0xff))
            }
            println()
        }
    }

    override fun toString() = "Texture $width x $height handle=$handle"

    private data class LoadedImage(val width: Int, val height: Int, val buffer: ByteBuffer)

    companion object {

        private var boundHandle: Int? = null

        private fun load(file: File): LoadedImage {

            MemoryStack.stackPush().use { stack ->

                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                val channels = stack.mallocInt(1)

                STBImage.stbi_set_flip_vertically_on_load(true)
                val buffer = stbi_load(file.path, width, height, channels, 4)
                buffer ?: throw IOException("Failed to load texture from ${file.absoluteFile}")
                return LoadedImage(width.get(), height.get(), buffer)
            }
        }

        fun create(file: File): Texture {
            val loadedImage = load(file)
            return Texture(loadedImage.width, loadedImage.height, GL_RGBA, loadedImage.buffer, file)
        }
    }

}
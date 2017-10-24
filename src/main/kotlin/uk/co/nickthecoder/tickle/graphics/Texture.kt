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


class Texture(val width: Int, val height: Int, pixelFormat: Int, buffer: ByteBuffer, val file: File? = null)

    : Deletable, Renamable {

    val handle: Int = glGenTextures()

    init {
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, buffer)
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

    override fun toString() = "Texture $width x $height handle=$handle"

    companion object {

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

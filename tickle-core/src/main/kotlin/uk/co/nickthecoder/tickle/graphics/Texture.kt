/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Dependable
import uk.co.nickthecoder.tickle.util.Renamable
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer


class Texture(width: Int, height: Int, pixelFormat: Int, buffer: ByteBuffer?, var file: File? = null)

    : Deletable, Renamable {

    private var privateHandle: Int = glGenTextures()

    val handle: Int
        get() = privateHandle

    private var privateWidth = width

    private var privateHeight = height

    val width: Int
        get() = privateWidth

    val height: Int
        get() = privateHeight

    init {
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, buffer)
    }

    fun reload() {
        file?.let {
            val loadedImage = load(it)
            write(loadedImage.width, loadedImage.height, loadedImage.buffer)
        }
    }

    /**
     * Writes a new image into the Texture
     */
    fun write(width: Int, height: Int, buffer: ByteBuffer, pixelFormat: Int = GL_RGBA) {
        unbind()
        // TODO Do we need to delete the old texture? Can we just do the final glTexImage2D Instead?
        glDeleteTextures(handle)
        privateHandle = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, handle)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, buffer)
        privateWidth = width
        privateHeight = height
    }

    /**
     * Transfers the texture from the GPU back to main memory (which is SLOW).
     * The result is a ByteArray of size width * height * 4, and the format is RGBA.
     * i.e. to get the alpha value at x,y :
     *
     *     read()[ (y * height + x) * 4 + 3 ]
     *
     * and then deal with the annoyance of java's lack of unsigned bytes. Grr :
     *
     *     .toInt() & 0xFF
     */
    fun read(flip: Boolean = false): ByteArray {
        bind()
        val pixels = ByteArray(width * height * 4)
        val buffer = ByteBuffer.allocateDirect(pixels.size)
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        buffer.get(pixels)
        if (flip) {
            for (y in 0 until height / 2) {
                for (x in 0 until width) {
                    for (c in 0..3) {
                        val tmp = pixels[((height - 1 - y) * width + x) * 4 + c]
                        pixels[((height - 1 - y) * width + x) * 4 + c] = pixels[(y * width + x) * 4 + c]
                        pixels[(y * width + x) * 4 + c] = tmp
                    }
                }
            }
        }
        return pixels
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

    fun destroy() {
        if (privateHandle != 0) {
            unbind()
            glDeleteTextures(privateHandle)
            privateHandle = 0
        }
    }

    /*
    protected fun finalize() {
        destroy()
    }
    */

    // Deletable
    /**
     * Can only delete if there are no Poses using this texture.
     */
    override fun dependables(): List<Dependable> {
        return Resources.instance.poses.items().values.filter { it.texture == this }
    }

    override fun delete() {
        Resources.instance.textures.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.textures.rename(this, newName)
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

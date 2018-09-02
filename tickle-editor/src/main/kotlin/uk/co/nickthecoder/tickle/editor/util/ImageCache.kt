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
package uk.co.nickthecoder.tickle.editor.util

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import org.lwjgl.opengl.GL11.*
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File
import java.nio.ByteBuffer


object ImageCache {

    private val cache = mutableMapOf<String, Image>()

    fun image(file: File): Image {
        val path = file.canonicalPath
        var image = cache[path]
        if (image == null) {
            file.inputStream().use {
                image = Image(it)
                cache[path] = image!!
            }
        }
        return image!!
    }

    private val textureCache = mutableMapOf<Int, Image>()

    fun clear() {
        cache.clear()
    }

    fun image(texture: Texture): Image {
        textureCache[texture.handle]?.let { return it }

        val width = texture.width
        val height = texture.height

        val pixels = ByteArray(width * height * 4)
        val buffer = ByteBuffer.allocateDirect(pixels.size)
        texture.bind()
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        buffer.get(pixels)

        val image = WritableImage(texture.width, texture.height)
        val writer = image.pixelWriter
        for (y in 0..texture.height - 1) {
            for (x in 0..texture.width - 1) {
                val i = (x + (height - y - 1) * texture.width) * 4
                val red = pixels[i].toInt() and 0xFF
                val green: Int = pixels[i + 1].toInt() and 0xFF
                val blue: Int = pixels[i + 2].toInt() and 0xFF
                val alpha = pixels[i + 3].toInt() and 0xFF
                val pixel = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
                writer.setArgb(x, y, pixel)
            }
        }
        textureCache[texture.handle] = image
        return image
    }

}

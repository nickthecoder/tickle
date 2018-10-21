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
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File


object ImageCache {

    private val cache = mutableMapOf<String, Image>()

    private val textureCache = mutableMapOf<Int, Image>()

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

    fun image(texture: Texture): Image {
        textureCache[texture.handle]?.let { return it }

        val image = texture.toImage()
        textureCache[texture.handle] = image
        return image
    }

    fun clear() {
        cache.clear()
        textureCache.clear()
    }

}

package uk.co.nickthecoder.tickle.editor

import javafx.scene.image.Image
import java.io.File

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
}

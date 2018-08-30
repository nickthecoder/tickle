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
package uk.co.nickthecoder.tickle.editor

import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import uk.co.nickthecoder.paratask.gui.ApplicationAction

class EditorAction(
        name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        tooltip: String? = null,
        label: String? = null) : ApplicationAction(name, keyCode, shift, control, alt, meta, shortcut, tooltip, label) {

    override val image: Image? = imageResource("$name.png")


    companion object {

        private val imageMap = mutableMapOf<String, Image?>()

        fun imageResource(name: String): Image? {
            val image = imageMap[name]
            if (image == null) {
                val imageStream = EditorAction::class.java.getResourceAsStream(name)
                val newImage = if (imageStream == null) null else Image(imageStream)
                imageMap.put(name, newImage)
                return newImage
            }
            return image
        }
    }
}

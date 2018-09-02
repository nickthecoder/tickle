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
package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.control.TextArea
import uk.co.nickthecoder.tickle.editor.ScriptStub
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.ResourcesListener
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import java.io.File

class ScriptTab(val scriptStub: ScriptStub)

    : EditTab(scriptStub.file.nameWithoutExtension, scriptStub, graphicName = ResourceType.SCRIPT.graphicName),
        ResourcesListener {

    val textArea = TextArea()
    val file = scriptStub.file

    init {
        textArea.text = file.readText()
        textArea.styleClass.add("script")
        borderPane.center = textArea
        Resources.instance.listeners.add(this)
    }

    override fun save(): Boolean {
        file.writeText(textArea.text)
        ScriptManager.load(file)
        return true
    }

    override fun resourceRemoved(resource: Any, name: String) {
        if (resource is File && resource == scriptStub.file) {
            close()
        }
    }
}

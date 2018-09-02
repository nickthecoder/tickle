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
import uk.co.nickthecoder.tickle.scripts.ScriptManager

class ScriptTab(val scriptStub: ScriptStub)

    : EditTab(scriptStub.file.nameWithoutExtension, scriptStub, graphicName = ResourceType.SCRIPT.graphicName) {

    val textArea = TextArea()
    val file = scriptStub.file

    init {
        textArea.text = file.readText()
        textArea.styleClass.add("script")
        borderPane.center = textArea
    }

    override fun save(): Boolean {
        file.writeText(textArea.text)
        ScriptManager.reload(file)
        return true
    }
}

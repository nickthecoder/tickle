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

package uk.co.nickthecoder.tickle.editor.tabs

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.FontResource
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.util.FontParameter

class FontTab(name: String, val fontResource: FontResource)

    : EditTaskTab(FontTask(name, fontResource), name, data = fontResource, graphicName = "font.png") {

    init {
        addDeleteButton { Resources.instance.deleteFontResource(name) }
    }

}

class FontTask(val name: String, val fontResource: FontResource) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val fontP = FontParameter("font")
    override val taskD = TaskDescription("editFont")
            .addParameters(nameP, fontP)

    init {
        fontP.from(fontResource)
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.renameFontResource(name, nameP.value)
        }

        fontP.update(fontResource)
    }

}

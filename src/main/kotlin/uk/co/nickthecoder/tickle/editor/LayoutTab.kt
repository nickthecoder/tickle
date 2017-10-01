package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.Layout

class LayoutTab(name: String, layout: Layout)

    : TaskTab(LayoutTask(name, layout), "Layout $name", layout) {

}

class LayoutTask(val name: String, val layout: Layout) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    override val taskD = TaskDescription("editPose")
            .addParameters(nameP)

    init {
    }

    override fun run() {
        if (nameP.value != name) {
            // TODO Rename!
        }
    }
}

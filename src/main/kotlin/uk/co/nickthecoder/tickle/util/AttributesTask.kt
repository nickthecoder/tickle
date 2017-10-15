package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.tickle.Attributes

class AttributesTask(classString: String, val attributes: Attributes) : AbstractTask() {

    override val taskD = TaskDescription("attributes")

    init {
        attributes.data().forEach { data ->
            data.parameter?.let { taskD.addParameters(it) }
        }
    }

    override fun run() {
    }
}

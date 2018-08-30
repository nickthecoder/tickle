package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.tickle.editor.util.DesignAttributeData
import uk.co.nickthecoder.tickle.editor.util.DesignAttributes

class AttributesTask(val attributes: DesignAttributes) : AbstractTask() {

    override val taskD = TaskDescription("attributes")

    init {
        attributes.data().forEach { data ->
            data as DesignAttributeData
            data.parameter?.let { taskD.addParameters(it) }
        }
    }

    override fun run() {
    }
}

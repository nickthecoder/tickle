package uk.co.nickthecoder.tickle.editor.util

import javafx.application.Platform
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.util.Renamable
import uk.co.nickthecoder.tickle.util.ResourceType

class RenameResourceTask(val resource: Renamable, type: ResourceType = ResourceType.ANY, oldName: String)

    : AbstractTask() {

    val newNameP = StringParameter("newName", value = oldName)

    override val taskD = TaskDescription("rename" + type.label)
            .addParameters(newNameP)

    override fun run() {
        Platform.runLater {
            resource.rename(newNameP.value)
        }
    }

}

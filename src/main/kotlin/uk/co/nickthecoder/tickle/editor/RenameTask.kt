package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import java.io.File


class RenameTask(file: File) : AbstractTask() {

    override val taskRunner = UnthreadedTaskRunner(this)

    val oldNameP = FileParameter("oldName", value = file)
    val newNameP = FileParameter("newName", value = file, mustExist = false)
    val infoP = InformationParameter("info", information = "Note. This will automatically save the project.")

    override val taskD = TaskDescription("renameFile", width = 600)
            .addParameters(oldNameP, newNameP, infoP)

    init {
        oldNameP.enabled = false
    }

    override fun run() {
        if (oldNameP.value != newNameP.value) {
            oldNameP.value!!.renameTo(newNameP.value!!)
        }
    }
}

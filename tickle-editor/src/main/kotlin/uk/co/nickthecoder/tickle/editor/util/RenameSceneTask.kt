package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.resources.Resources
import java.io.File


class RenameSceneTask(val file: File) : AbstractTask(threaded = false) {
    val newNameP = StringParameter("newName", value = file.nameWithoutExtension)
    val directoryP = FileParameter("directory", mustExist = true, expectFile = false, value = file.parentFile)

    override val taskD = TaskDescription("renameScene")
            .addParameters(newNameP, directoryP)

    override fun run() {
        val newFile = newFile()
        file.renameTo(newFile)
        Resources.instance.fireRemoved(file, file.nameWithoutExtension)
        Resources.instance.fireAdded(newFile, newFile.nameWithoutExtension)
    }

    fun newFile() = File(directoryP.value!!, "${newNameP.value}.scene")
}

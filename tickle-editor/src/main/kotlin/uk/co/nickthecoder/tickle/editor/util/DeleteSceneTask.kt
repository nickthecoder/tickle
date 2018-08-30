package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.tickle.resources.Resources
import java.io.File


class DeleteSceneTask(val file: File) : AbstractTask(threaded = false) {

    override val taskD = TaskDescription("deleteScene", description = "Delete scene ${file.nameWithoutExtension}. Are you sure?")

    override fun run() {
        file.delete()
        Resources.instance.fireRemoved(file, file.nameWithoutExtension)
    }
}

package uk.co.nickthecoder.tickle.editor.util

import javafx.application.Platform
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.editor.resources.DesignResources
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import java.io.File

class RenameScriptTask(private val oldFile: File)

    : AbstractTask() {

    val newNameP = StringParameter("newName", value = oldFile.nameWithoutExtension)

    override val taskD = TaskDescription("renameScript")
            .addParameters(newNameP)

    override fun run() {
        Platform.runLater {
            val contents = oldFile.readText()
            val oldName = oldFile.nameWithoutExtension
            val newFile = File(oldFile.parentFile, "${newNameP.value}.${oldFile.extension}")

            oldFile.delete()
            newFile.writeText(contents.replaceFirst("class ${oldName}", "class ${newNameP.value}"))

            Resources.instance.costumes.items().values.filter { it.roleString == oldName }.forEach {
                it.roleString = newNameP.value
            }

            Resources.instance.fireRemoved(oldFile, oldName)
            Resources.instance.fireAdded(newFile, newNameP.value)
            ScriptManager.load(newFile)
            (Resources.instance as DesignResources).save() // In case a Costume was changed
        }
    }
}

package uk.co.nickthecoder.tickle.editor.tabs

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.TextureResource
import uk.co.nickthecoder.tickle.editor.util.RenameTask

class TextureTab(name: String, textureResource: TextureResource)
    : EditTaskTab(TextureTask(name, textureResource), "Texture", name, textureResource) {

    init {
        addDeleteButton { Resources.instance.deleteTexture(name) }
    }

}

class TextureTask(val name: String, val textureResource: TextureResource) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val filenameP = StringParameter("filename", value = textureResource.file.path)
    val renameP = ButtonParameter("rename", buttonText = "Rename") { onRename() }
    val viewP = ButtonParameter("view", buttonText = "View") { onView() }
    val editP = ButtonParameter("edit", buttonText = "Edit") { onEdit() }
    val buttonsP = SimpleGroupParameter("buttons", label = "")
            .addParameters(renameP, viewP, editP).asGrid(labelPosition = LabelPosition.NONE)

    override val taskD = TaskDescription("editTexture")
            .addParameters(nameP, filenameP, buttonsP)

    init {
        filenameP.enabled = false
    }

    override fun customCheck() {
        val tr = Resources.instance.optionalTextureResource(nameP.value)
        if (tr != null && tr != textureResource) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.renameTexture(name, nameP.value)
        }
    }

    fun onRename() {
        val renameTask = RenameTask(textureResource.file)
        try {
            check()
        } catch (e: Exception) {
            return
        }
        renameTask.taskRunner.listen { cancelled ->
            if (!cancelled) {
                renameTask.newNameP.value?.path?.let { filenameP.value = it }
                run()
                Resources.instance.save()
            }
        }
        val tp = TaskPrompter(renameTask)
        tp.placeOnStage(Stage())
    }

    fun onView() {
        val exec = Exec("gwenview", textureResource.file)
        exec.start()
    }

    fun onEdit() {
        val exec = Exec("gimp", textureResource.file)
        exec.start()
    }
}

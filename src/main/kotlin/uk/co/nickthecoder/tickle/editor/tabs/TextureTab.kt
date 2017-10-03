package uk.co.nickthecoder.tickle.editor.tabs

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.util.RenameTask
import uk.co.nickthecoder.tickle.graphics.Texture

class TextureTab(name: String, texture: Texture)
    : EditTaskTab(TextureTask(name, texture), "Texture", name, texture) {

    init {
        addDeleteButton { Resources.instance.deleteTexture(name) }
    }

}

class TextureTask(val name: String, val texture: Texture) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val filenameP = StringParameter("filename", value = texture.file?.path ?: "")
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
        val t = Resources.instance.optionalTexture(nameP.value)
        if (t != null && t != texture) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.renameTexture(name, nameP.value)
        }
    }

    fun onRename() {
        texture.file?.let { file ->
            val renameTask = RenameTask(file)
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
    }

    fun onView() {
        val exec = Exec("gwenview", texture.file)
        exec.start()
    }

    fun onEdit() {
        val exec = Exec("gimp", texture.file)
        exec.start()
    }
}

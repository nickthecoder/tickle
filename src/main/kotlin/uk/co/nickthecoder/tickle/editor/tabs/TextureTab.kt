package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.control.Button
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.ImageCache
import uk.co.nickthecoder.tickle.editor.util.ImageParameter
import uk.co.nickthecoder.tickle.editor.util.RenameTask
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

class TextureTab(name: String, val texture: Texture)
    : EditTaskTab(TextureTask(name, texture), "Texture", name, texture) {

    init {
        addDeleteButton { Resources.instance.deleteTexture(name) }
        val editButton = Button("Edit")
        editButton.setOnAction { edit() }
        leftButtons.children.add(editButton)
    }

    fun edit() {
        texture.file?.absoluteFile?.let { file ->
            val xcf = File(file.parentFile, file.nameWithoutExtension + ".xcf")
            if (xcf.exists()) {
                Exec("gimp", xcf).start()
            } else {
                val svg = File(file.parentFile, file.nameWithoutExtension + ".svg")
                if (svg.exists()) {
                    Exec("inkscape", svg).start()
                } else {
                    Exec("gimp", file).start()
                }
            }
        }
    }

}

class TextureTask(val name: String, val texture: Texture) : AbstractTask() {

    val nameP = StringParameter("name", value = name)


    val fileP = StringParameter("file", value = texture.file?.path ?: "")
    val renameP = ButtonParameter("rename", buttonText = "Rename") { onRename() }
    val fileAndRenameP = SimpleGroupParameter("fileAndRename", label = "Filename")
            .addParameters(fileP, renameP).asHorizontal(labelPosition = LabelPosition.NONE)

    val imageP = ImageParameter("image", image = ImageCache.image(texture.file!!))

    override val taskD = TaskDescription("editTexture")
            .addParameters(nameP, fileAndRenameP, imageP)

    init {
        fileP.enabled = false
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
                    renameTask.newNameP.value?.path?.let { fileP.value = it }
                    run()
                    Resources.instance.save()
                }
            }
            val tp = TaskPrompter(renameTask)
            tp.placeOnStage(Stage())
        }
    }

}

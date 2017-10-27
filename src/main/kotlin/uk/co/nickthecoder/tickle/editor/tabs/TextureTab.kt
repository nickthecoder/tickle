package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.TabPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.tickle.editor.util.ImageCache
import uk.co.nickthecoder.tickle.editor.util.ImageParameter
import uk.co.nickthecoder.tickle.editor.util.RenameFileTask
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.Resources
import java.io.File

class TextureTab(name: String, val texture: Texture)

    : EditTab(name, texture, graphicName = "texture.png") {

    val task = TextureTask(name, texture)
    val taskForm = TaskForm(task)

    val posesEditor = PosesEditor(texture)

    val minorTabs = MyTabPane<MyTab>()

    val detailsTab = MyTab("Details", taskForm.build())
    val posesTab = MyTab("Poses", posesEditor.build())

    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(detailsTab)
        minorTabs.add(posesTab)
        borderPane.center = minorTabs

        addDeleteButton { Resources.instance.textures.remove(name) }
        val editButton = Button("Edit")
        editButton.setOnAction { edit() }
        leftButtons.children.add(editButton)
    }

    override fun removed() {
        posesEditor.closed()
    }

    override fun save(): Boolean {
        val result = taskForm.check()
        if (result) {
            task.run()
        }
        return result
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
        val t = Resources.instance.textures.find(nameP.value)
        if (t != null && t != texture) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.textures.rename(name, nameP.value)
        }
    }

    // TODO Renaming the file doesn't change the Texture object

    fun onRename() {
        texture.file?.let { file ->
            val renameTask = RenameFileTask(file)
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

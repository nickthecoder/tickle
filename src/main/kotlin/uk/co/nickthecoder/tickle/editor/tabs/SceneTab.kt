package uk.co.nickthecoder.tickle.editor.tabs

import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.TabPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.ColorParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.demo.Director
import uk.co.nickthecoder.tickle.demo.NoDirector
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.SceneStub
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.util.JsonScene
import java.io.File

class SceneTab(val sceneName: String, sceneStub: SceneStub)

    : EditTab(sceneName, sceneStub, graphicName = "scene.png"),
        HasExtras {

    val sceneResource = JsonScene(sceneStub.file, isDesigning = true).sceneResource

    private val task = SceneDetailsTask(sceneName, sceneResource)
    private val taskForm = TaskForm(task)

    private val sceneEditor = SceneEditor(sceneResource)

    private val minorTabs = MyTabPane<MyTab>()

    private val detailsTab = MyTab("Details", taskForm.build())
    private val editorTab = MyTab("Scene Editor", sceneEditor.build())

    private val testButton = Button("Test")

    private val copyButton = Button("Copy")

    private val renameButton = Button("Rename")

    private val deleteButton = Button("Delete")

    val sceneFile = sceneStub.file


    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(detailsTab)
        minorTabs.add(editorTab)
        borderPane.center = minorTabs

        editorTab.isSelected = true

        applyButton.text = "Save"
        rightButtons.children.remove(okButton)
        rightButtons.children.add(0, testButton)
        testButton.onAction = EventHandler { onTest() }

        leftButtons.children.addAll(copyButton, renameButton, deleteButton)

        copyButton.onAction = EventHandler { onCopy() }
        deleteButton.onAction = EventHandler { onDelete() }
        renameButton.onAction = EventHandler { onRename() }
    }

    override fun extraSidePanes() = sceneEditor.sidePanes

    override fun extraButtons() = listOf(sceneEditor.layers.stageButton)

    override fun save(): Boolean {
        if (taskForm.check()) {
            task.run()
            JsonScene(sceneResource).save(sceneResource.file!!)
            return true
        }
        return false
    }

    fun onTest() {
        Resources.instance.gameInfo.testScenePath = sceneResource.file!!
        if (save()) {
            MainWindow.instance.startGame(sceneFile)
        }
    }

    fun onCopy() {
        TaskPrompter(CopySceneTask()).placeOnStage(Stage())
    }

    fun onDelete() {
        TaskPrompter(DeleteSceneTask()).placeOnStage(Stage())
    }

    fun onRename() {
        TaskPrompter(RenameSceneTask()).placeOnStage(Stage())
    }

    override fun removed() {
        super.removed()
        sceneEditor.cleanUp()
    }

    fun selectCostumeName(costumeName: String) {
        sceneEditor.selectCostumeName(costumeName)
    }


    inner class CopySceneTask : AbstractTask(threaded = false) {

        val newNameP = StringParameter("newName")
        val directoryP = FileParameter("directory", mustExist = true, expectFile = false, value = sceneResource.file?.parentFile)

        override val taskD = TaskDescription("copyScene")
                .addParameters(newNameP, directoryP)

        override fun run() {
            val newFile = File(directoryP.value!!, "${newNameP.value}.scene")
            sceneResource.file!!.copyTo(newFile)
            Resources.instance.fireAdded(newFile, newFile.nameWithoutExtension)
        }
    }

    inner class DeleteSceneTask : AbstractTask(threaded = false) {

        override val taskD = TaskDescription("deleteScene", description = "Delete scene ${sceneName}. Are you sure?")

        override fun run() {
            val oldFile = sceneResource.file!!
            oldFile.delete()
            Resources.instance.fireRemoved(oldFile, oldFile.nameWithoutExtension)
            close()
        }
    }

    inner class RenameSceneTask() : AbstractTask(threaded = false) {
        val newNameP = StringParameter("newName")
        val directoryP = FileParameter("directory", mustExist = true, expectFile = false, value = sceneResource.file?.parentFile)

        override val taskD = TaskDescription("renameScene")
                .addParameters(newNameP, directoryP)

        override fun run() {
            val oldFile = sceneResource.file!!
            val newFile = File(directoryP.value!!, "${newNameP.value}.scene")

            oldFile.renameTo(newFile)
            Resources.instance.fireRemoved(oldFile, oldFile.nameWithoutExtension)
            Resources.instance.fireAdded(newFile, newFile.nameWithoutExtension)
            sceneResource.file = newFile
            MainWindow.instance.openTab(newFile.nameWithoutExtension, SceneStub(newFile))
            close()
        }
    }

}


class SceneDetailsTask(val name: String, val sceneResource: SceneResource) : AbstractTask() {

    val directorP = ChoiceParameter<Class<*>>("director", required = false, value = NoDirector::class.java)

    val backgroundColorP = ColorParameter("backgroundColor")

    val layoutP = ChoiceParameter<String>("layout", value = "")

    override val taskD = TaskDescription("sceneDetails")
            .addParameters(directorP, backgroundColorP, layoutP)

    init {
        ClassLister.setChoices(directorP, Director::class.java)
        Resources.instance.layouts().forEach { name, _ ->
            layoutP.choice(name, name, name)
        }

        try {
            directorP.value = Class.forName(sceneResource.directorString)
        } catch (e: Exception) {
            //
        }
        val c = sceneResource.background
        backgroundColorP.value = javafx.scene.paint.Color(c.red.toDouble(), c.green.toDouble(), c.blue.toDouble(), 1.0)
        layoutP.value = sceneResource.layoutName
    }


    override fun run() {
        sceneResource.directorString = directorP.value!!.name
        sceneResource.layoutName = layoutP.value!!
        val c = backgroundColorP.value
        sceneResource.background = uk.co.nickthecoder.tickle.graphics.Color(c.red.toFloat(), c.green.toFloat(), c.blue.toFloat())
    }
}

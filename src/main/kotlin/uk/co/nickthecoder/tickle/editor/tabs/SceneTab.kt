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
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.tickle.Director
import uk.co.nickthecoder.tickle.NoDirector
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.SceneStub
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.util.*
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.SceneResource
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

    override fun extraButtons() = listOf(sceneEditor.guidesButton, sceneEditor.gridButton, sceneEditor.layers.stageButton)

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
        val task = DeleteSceneTask(sceneFile)
        task.taskRunner.listen { cancelled ->
            if (!cancelled) {
                close()
            }
        }
        TaskPrompter(task).placeOnStage(Stage())
    }

    fun onRename() {
        val task = RenameSceneTask(sceneFile)
        task.taskRunner.listen { cancelled ->
            if (!cancelled) {
                close()
                sceneResource.file = task.newFile()
                MainWindow.instance.openTab(task.newNameP.value, sceneResource)
            }
        }
        TaskPrompter(task).placeOnStage(Stage())
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


}


class SceneDetailsTask(val name: String, val sceneResource: SceneResource) : AbstractTask() {

    val directorP = ChoiceParameter<Class<*>>("director", required = false, value = NoDirector::class.java)

    val backgroundColorP = ColorParameter("backgroundColor")

    val showMouseP = BooleanParameter("showMouse")

    val layoutP = ChoiceParameter<String>("layout", value = "")

    val infoP = InformationParameter("info",
            information = "The Director has no fields with the '@Attribute' annotation, and therefore, this scene has no attributes.")

    val includesP = MultipleParameter("includes") {
        StringParameter("include")
    }

    val attributesP = SimpleGroupParameter("attributes")

    override val taskD = TaskDescription("sceneDetails")
            .addParameters(directorP, backgroundColorP, showMouseP, layoutP, includesP, attributesP)

    init {
        ClassLister.setChoices(directorP, Director::class.java)
        Resources.instance.layouts.items().forEach { name, _ ->
            layoutP.choice(name, name, name)
        }

        try {
            directorP.value = Class.forName(sceneResource.directorString)
        } catch (e: Exception) {
            //
        }
        with(sceneResource) {
            backgroundColorP.value = background.toJavaFX()
            showMouseP.value = showMouse
            layoutP.value = layoutName
            includes.forEach { file ->
                includesP.addValue(Resources.instance.sceneFileToPath(file))
            }
            println("Includes = $includes")
        }

        updateAttributes()
        directorP.listen {
            updateAttributes()
        }
    }


    override fun run() {
        with(sceneResource) {
            directorString = directorP.value!!.name
            layoutName = layoutP.value!!
            showMouse = showMouseP.value == true
            background = backgroundColorP.value.toTickle()
            includes.clear()
            includesP.value.forEach { str ->
                includes.add(File(str))
            }
        }
    }

    fun updateAttributes() {
        sceneResource.directorAttributes.updateAttributesMetaData(directorP.value!!.name, true)
        attributesP.children.toList().forEach {
            attributesP.remove(it)
        }

        sceneResource.directorAttributes.data().forEach { data ->
            data.parameter?.let { it ->
                val parameter = it.copyBounded()
                attributesP.add(parameter)
                try {
                    parameter.stringValue = data.value ?: ""
                } catch (e: Exception) {
                    // Do nothing
                }
            }
        }

        if (attributesP.children.size == 0) {
            attributesP.add(infoP)
        }

    }

}

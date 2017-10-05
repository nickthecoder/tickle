package uk.co.nickthecoder.tickle.editor.tabs

import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.ColorParameter
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.demo.Director
import uk.co.nickthecoder.tickle.demo.NoDirector
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.SceneStub
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.util.JsonScene

class SceneTab(val sceneName: String, sceneStub: SceneStub)

    : EditTab("Texture", sceneName, sceneStub) {

    val sceneResource = JsonScene(sceneStub.file).sceneResource

    val task = SceneDetailsTask(sceneName, sceneResource)
    val taskForm = TaskForm(task)

    val sceneEditor = SceneEditor(sceneResource)

    val minorTabs = MyTabPane<MyTab>()

    val detailsTab = MyTab("Details", taskForm.build())
    val editorTab = MyTab("Scene Editor", sceneEditor.build())

    val testButton = Button("Test")

    val sceneFile = sceneStub.file

    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(detailsTab)
        minorTabs.add(editorTab)
        borderPane.center = minorTabs

        // TODO Add a delete button (the following won't work!
        // addDeleteButton { Resources.instance.deleteTexture(name) }
        // TODO Add a RENAME button.
        // fire Add and remove events

        editorTab.isSelected = true

        applyButton.text = "Save"
        rightButtons.children.remove(okButton)
        rightButtons.children.add(0, testButton)
        testButton.onAction = EventHandler { test() }
    }

    override fun save(): Boolean {
        if (taskForm.check()) {
            task.run()
            JsonScene(sceneResource).save(sceneResource.file!!)
            return true
        }
        return false
    }

    fun test() {
        MainWindow.instance?.startGame(sceneFile)
    }

    override fun removed() {
        super.removed()
        sceneEditor.cleanUp()
    }

    fun selectCostume(costume: Costume) {
        sceneEditor.selectCostume(costume)
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

package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

class SnapEditor(val snapTos: Map<String, HasTask>) {

    val whole = BorderPane()

    val tabPane = MyTabPane<MyTab>()

    val buttons = FlowPane()

    val okButton = Button("Ok")

    val cancelButton = Button("Cancel")

    val taskForms = mutableListOf<TaskForm>()

    val stage = Stage()

    fun show() {

        with(okButton) {
            okButton.onAction = EventHandler { onOk() }
            okButton.isDefaultButton = true
        }

        with(cancelButton) {
            cancelButton.onAction = EventHandler { onCancel() }
            cancelButton.isCancelButton = true
        }

        with(buttons) {
            children.addAll(okButton, cancelButton)
            styleClass.add("buttons")
        }

        snapTos.forEach { name, snapTo ->
            val form = TaskForm(snapTo.task())
            tabPane.add(MyTab(name, form.build()))
            taskForms.add(form)
        }

        with(whole) {
            center = tabPane
            bottom = buttons
        }

        val scene = Scene(whole)
        ParaTask.style(scene)

        with(stage) {
            title = "Snapping"
            this.scene = scene
            centerOnScreen()
            show()
        }
    }

    fun onOk() {
        try {
            taskForms.forEach { form ->
                form.check()
            }
            taskForms.forEach { form ->
                form.task.run()
            }
        } catch (e: Exception) {
            // Do nothing
        }
    }

    fun onCancel() {
        stage.close()
    }
}

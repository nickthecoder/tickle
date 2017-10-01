package uk.co.nickthecoder.tickle.editor

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

open class TaskTab(val task: Task, text: String, data: Any, graphic: Node? = null)

    : EditorTab(text, data, graphic) {

    val borderPane = BorderPane()

    private val buttons = FlowPane()

    val okButton = Button("Ok")

    val applyButton = Button("Apply")

    val cancelButton = Button("Cancel")

    val taskForm = TaskForm(task)

    init {

        with(borderPane) {
            styleClass.add("prompt")
            center = taskForm.scrollPane
            bottom = buttons
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            isCancelButton = true
        }

        with(applyButton) {
            onAction = EventHandler { onApply() }
            isCancelButton = true
        }

        with(okButton) {
            onAction = EventHandler { onOk() }
            isDefaultButton = true
        }


        with(buttons) {
            children.addAll(okButton, applyButton, cancelButton)
            styleClass.add("buttons")
        }

        taskForm.build()
        content = borderPane
    }

    private fun onCancel() {
        tabPane?.remove(this)
    }

    private fun onApply() {
        if (taskForm.check()) {
            task.run()
        }
    }

    protected fun onOk() {
        if (taskForm.check()) {
            task.run()
        }
        tabPane?.remove(this)
    }

}

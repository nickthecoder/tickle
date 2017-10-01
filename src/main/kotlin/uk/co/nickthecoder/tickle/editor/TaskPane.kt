package uk.co.nickthecoder.tickle.editor

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

open class TaskPane(val tab: EditorTab, val task: Task) {

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
    }

    private fun onCancel() {
        tab.tabPane?.remove(tab)
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
        tab.tabPane?.remove(tab)
    }

}

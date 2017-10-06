package uk.co.nickthecoder.tickle.editor.tabs

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

open class EditTaskTab(
        val task: Task,
        dataName: String,
        data: Any,
        graphicName : String? = null)

    : EditTab(dataName, data, graphicName=graphicName) {

    val taskForm = TaskForm(task)

    init {
        borderPane.center = taskForm.build()
    }

    override fun save(): Boolean {
        if (taskForm.check()) {
            task.run()
            return true
        }
        return false
    }
}

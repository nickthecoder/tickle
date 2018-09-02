/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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

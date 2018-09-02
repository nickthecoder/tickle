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
package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import java.io.File


class RenameFileTask(file: File) : AbstractTask() {

    override val taskRunner = UnthreadedTaskRunner(this)

    val oldNameP = FileParameter("oldName", value = file)
    val newNameP = FileParameter("newName", value = file, mustExist = false)
    val infoP = InformationParameter("info", information = "Note. This will automatically save the project.")

    override val taskD = TaskDescription("renameFile", width = 600)
            .addParameters(oldNameP, newNameP, infoP)

    init {
        oldNameP.enabled = false
    }

    override fun run() {
        if (oldNameP.value != newNameP.value) {
            oldNameP.value!!.renameTo(newNameP.value!!)
        }
    }
}

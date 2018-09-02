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
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.resources.Resources
import java.io.File


class RenameSceneTask(val file: File) : AbstractTask(threaded = false) {
    val newNameP = StringParameter("newName", value = file.nameWithoutExtension)
    val directoryP = FileParameter("directory", mustExist = true, expectFile = false, value = file.parentFile)

    override val taskD = TaskDescription("renameScene")
            .addParameters(newNameP, directoryP)

    override fun run() {
        val newFile = newFile()
        file.renameTo(newFile)
        Resources.instance.fireRemoved(file, file.nameWithoutExtension)
        Resources.instance.fireAdded(newFile, newFile.nameWithoutExtension)
    }

    fun newFile() = File(directoryP.value!!, "${newNameP.value}.scene")
}

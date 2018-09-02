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

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.editor.resources.DesignResources
import uk.co.nickthecoder.tickle.editor.util.RenameFileTask
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.sound.Sound

class SoundTab(name: String, sound: Sound)
    : EditTaskTab(SoundTask(name, sound), name, sound, graphicName = "sound.png") {

}

class SoundTask(val name: String, val sound: Sound) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val fileP = StringParameter("file", value = sound.file?.path ?: "")
    val renameP = ButtonParameter("rename", buttonText = "Rename") { onRename() }
    val fileAndRenameP = SimpleGroupParameter("fileAndRename", label = "Filename")
            .addParameters(fileP, renameP).asHorizontal(labelPosition = LabelPosition.NONE)

    override val taskD = TaskDescription("editSound")
            .addParameters(nameP, fileAndRenameP)


    override fun customCheck() {
        val p = Resources.instance.sounds.find(nameP.value)
        if (p != null && p != sound) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        //println("Run LTRB : ${positionP.left},${positionP.top}, ${positionP.right}, ${positionP.bottom}  size : ${positionP.width}, ${positionP.height}")
        if (nameP.value != name) {
            Resources.instance.sounds.rename(name, nameP.value)
        }
    }

    // TODO Renaming the file doesn't change the Sound object

    fun onRename() {
        sound.file?.let { file ->
            val renameTask = RenameFileTask(file)
            try {
                check()
            } catch (e: Exception) {
                return
            }
            renameTask.taskRunner.listen { cancelled ->
                if (!cancelled) {
                    renameTask.newNameP.value?.path?.let { fileP.value = it }
                    run()
                    (Resources.instance as DesignResources).save()
                }
            }
            val tp = TaskPrompter(renameTask)
            tp.placeOnStage(Stage())
        }
    }

}

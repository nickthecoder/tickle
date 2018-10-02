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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.resources.Resources

class CostumeGroupTab(name: String, costumeGroup: CostumeGroup)

    : EditTaskTab(CostumeGroupTask(name, costumeGroup), name, costumeGroup, graphicName = "directory2.png") {

}

class CostumeGroupTask(val name: String, val costumeGroup: CostumeGroup) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val showInSceneEditorP = BooleanParameter("showInSceneEditor", value = costumeGroup.showInSceneEditor)

    override val taskD = TaskDescription("editCostumeGroup")
            .addParameters(nameP, showInSceneEditorP)

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.costumeGroups.rename(name, nameP.value)
        }

        costumeGroup.showInSceneEditor = showInSceneEditorP.value == true
    }

}

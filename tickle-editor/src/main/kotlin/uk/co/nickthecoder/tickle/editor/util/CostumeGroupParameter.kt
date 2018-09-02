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

import javafx.application.Platform
import uk.co.nickthecoder.paratask.TaskListener
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.ResourcesListener

class CostumeGroupParameter(name: String = "group", val newGroupCallback: (String) -> Unit)

    : SimpleGroupParameter(name), TaskListener, ResourcesListener {

    val costumeP = ChoiceParameter<CostumeGroup?>(name + "_costume", value = null, required = false)

    val createGroupP = ButtonParameter(name + "_create", buttonText = "New Group") { createNewGroup() }

    val newTask = NewResourceTask(ResourceType.COSTUME_GROUP)

    init {
        addParameters(costumeP, createGroupP)
        asHorizontal(LabelPosition.NONE)
        createChoices()
        Resources.instance.listeners.add(this)
    }

    override fun resourceAdded(resource: Any, name: String) {
        resourceModified(resource)
    }

    fun resourceModified(resource: Any) {
        if (resource is CostumeGroup) {
            createChoices()
        }
    }

    fun createChoices() {
        val oldValue = costumeP.value
        costumeP.clear()
        costumeP.addChoice("", null, "None")
        Resources.instance.costumeGroups.items().forEach { groupName, costumeGroup ->
            costumeP.addChoice(groupName, costumeGroup, groupName)
        }
        costumeP.value = oldValue
    }

    fun createNewGroup() {
        newTask.taskRunner.listeners.add(this)
        newTask.prompt()
    }

    override fun ended(cancelled: Boolean) {
        if (!cancelled) {
            Platform.runLater {
                newGroupCallback(newTask.nameP.value)
            }
        }
    }

}

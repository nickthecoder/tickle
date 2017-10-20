package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.TaskListener
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.ResourcesListener

class CostumeGroupParameter(name: String = "group", val newGroupCallback: (String) -> Unit)

    : SimpleGroupParameter(name), TaskListener, ResourcesListener {

    val costumeP = ChoiceParameter<CostumeGroup?>(name + "_costume", value = null, required = false)

    val createGroupP = ButtonParameter(name + "_create", buttonText = "New Group") { createNewGroup() }

    val newTask = NewResourceTask(NewResourceTask.ResourceType.COSTUME_GROUP)

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
            newGroupCallback(newTask.nameP.value)
        }
    }

}

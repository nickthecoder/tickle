package uk.co.nickthecoder.tickle.editor.tabs

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.resources.Resources

class CostumeGroupTab(name: String, val costumeGroup: CostumeGroup)

    : EditTaskTab(CostumeGroupTask(name, costumeGroup), name, costumeGroup, graphicName = "directory2.png") {

    init {
        addDeleteButton { Resources.instance.textures.remove(name) }
    }
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

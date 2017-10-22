package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.TabPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.editor.util.ImageCache
import uk.co.nickthecoder.tickle.editor.util.ImageParameter
import uk.co.nickthecoder.tickle.editor.util.RenameTask
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.Resources
import java.io.File

class CostumeGroupTab(name: String, val costumeGroup: CostumeGroup)

    : EditTaskTab(CostumeGroupTask(name, costumeGroup), name, costumeGroup, graphicName = "directory2.png") {

    init {
        addDeleteButton { Resources.instance.textures.remove(name) }
    }
}

class CostumeGroupTask(val name: String, val costumeGroup: CostumeGroup) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    override val taskD = TaskDescription("editCostumeGroup")
            .addParameters(nameP)

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.costumeGroups.rename(name, nameP.value)
        }
    }

}

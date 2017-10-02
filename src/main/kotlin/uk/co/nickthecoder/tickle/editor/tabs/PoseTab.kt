package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.control.Alert
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ButtonParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.util.RectiParameter
import uk.co.nickthecoder.tickle.editor.util.XYParameter

class PoseTab(name: String, pose: Pose)
    : EditTaskTab(PoseTask(name, pose), "Pose", name, pose) {

    init {
        addDeleteButton { Resources.instance.deletePose(name) }
    }

}

class PoseTask(val name: String, val pose: Pose) : AbstractTask() {

    val nameP = StringParameter("name", value = name)
    val textureNameP = ButtonParameter("texture", buttonText = Resources.instance.findTextureName(pose.texture) ?: "<none>") {
        editTexture()
    }

    val positionP = RectiParameter("position", bottomUp = false)
    val offsetP = XYParameter("offset")

    val createCostumeP = ButtonParameter("createCostume", buttonText = "Create") { createCostume() }

    override val taskD = TaskDescription("editPose")
            .addParameters(nameP, textureNameP, positionP, offsetP, createCostumeP)

    init {
        offsetP.x = pose.offsetX.toDouble()
        offsetP.y = pose.offsetY.toDouble()

        positionP.left = pose.rect.left
        positionP.right = pose.rect.right
        positionP.top = pose.rect.top
        positionP.bottom = pose.rect.bottom

        // println("Init LTRB : ${positionP.left},${positionP.top}, ${positionP.right}, ${positionP.bottom}  size : ${positionP.width}, ${positionP.height}")

    }

    override fun customCheck() {
        val p = Resources.instance.optionalPose(nameP.value)
        if (p != null && p != pose) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        //println("Run LTRB : ${positionP.left},${positionP.top}, ${positionP.right}, ${positionP.bottom}  size : ${positionP.width}, ${positionP.height}")
        if (nameP.value != name) {
            Resources.instance.renamePose(name, nameP.value)
        }
        pose.rect.left = positionP.left!!
        pose.rect.bottom = positionP.bottom!!
        pose.rect.right = positionP.right!!
        pose.rect.top = positionP.top!!

        pose.offsetX = offsetP.x!!.toFloat()
        pose.offsetY = offsetP.y!!.toFloat()
    }

    fun editTexture() {
        val trName = Resources.instance.findTextureName(pose.texture)
        val tr = Resources.instance.findTextureResource(pose.texture)
        if (trName != null && tr != null) {
            val tab = TextureTab(trName, tr)
            MainWindow.instance?.tabPane?.add(tab)
            tab.isSelected = true
        }
    }

    fun createCostume() {
        val poseName = Resources.instance.findPoseName(pose)
        if ( poseName == null) {
            return
        }

        if (Resources.instance.optionalCostume(poseName) != null) {
            Alert(Alert.AlertType.INFORMATION, "A Costume called ${name} already exists.").showAndWait()
            return
        }

        val costume = Costume()
        costume.addPose("default", pose)
        Resources.instance.addCostume( poseName, costume )
        MainWindow.instance?.openTab(poseName, costume)
    }
}

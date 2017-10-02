package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.CompoundInput
import java.io.File

class NewResourceTask : AbstractTask() {

    val nameP = StringParameter("resourceName")

    val textureP = FileParameter("textureFile", label = "File", value = File(Resources.instance.file.parentFile, "images").absoluteFile)

    val poseP = createTextureParameter()

    val costumeP = createPoseParameter()

    val layoutP = InformationParameter("layout", label = "Layout", information = "")

    val inputP = InformationParameter("input", label = "Input", information = "")

    val resourceTypeP = OneOfParameter("resourceType", label = "Resource", choiceLabel = "Type")
            .addParameters("Texture" to textureP, "Pose" to poseP, "Costume" to costumeP, "Layout" to layoutP, "Input" to inputP)


    override val taskD = TaskDescription("newResource", height = 200)
            .addParameters(nameP, resourceTypeP)

    override val taskRunner = UnthreadedTaskRunner(this)


    override fun run() {

        val name = nameP.value
        var data: Any? = null

        when (resourceTypeP.value) {
            textureP -> {
                data = Resources.instance.addTexture(name, textureP.value!!)
            }
            poseP -> {
                val pose = Pose(poseP.value!!)
                Resources.instance.addPose(name, pose)
                data = pose
            }
            costumeP -> {
                val costume = Costume()
                costume.addPose("default", costumeP.value!!)
                Resources.instance.addCostume(name, costume)
                data = costume
            }
            layoutP -> {
                val layout = Layout()
                layout.stages["main"] = LayoutStage()
                layout.views["main"] = LayoutView(stageName = "main")
                Resources.instance.addLayout(name, layout)
                data = layout
            }
            inputP -> {
                val input = CompoundInput()
                Resources.instance.addInput(name, input)
                data = input
            }
        }

        if (data != null) {
            MainWindow.instance?.openTab(nameP.value, data)
        }
    }
}

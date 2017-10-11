package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.util.JsonScene
import java.io.File

class NewResourceTask : AbstractTask() {

    val textureP = FileParameter("textureFile", label = "File", value = File(Resources.instance.file.parentFile, "images").absoluteFile)

    val poseP = createTextureParameter()

    val costumeP = createPoseParameter()

    val layoutP = InformationParameter("layout", information = "")

    val inputP = InformationParameter("input", information = "")

    val fontP = FontParameter("font")

    val sceneDirectoryP = InformationParameter("sceneDirectory", information = "")

    val sceneP = FileParameter("scene", label = "Directory", expectFile = false, value = Resources.instance.sceneDirectory.absoluteFile)

    val resourceTypeP = OneOfParameter("resourceType", label = "Resource", choiceLabel = "Type")
            .addParameters(
                    "Texture" to textureP, "Pose" to poseP, "Font" to fontP, "Costume" to costumeP, "Layout" to layoutP,
                    "Input" to inputP, "Scene Directory" to sceneDirectoryP, "Scene" to sceneP)

    val nameP = StringParameter("resourceName")

    override val taskD = TaskDescription("newResource", height = 300)
            .addParameters(resourceTypeP, nameP)

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
                layout.layoutStages["main"] = LayoutStage()
                layout.layoutViews["main"] = LayoutView(stageName = "main")
                Resources.instance.addLayout(name, layout)
                data = layout
            }
            inputP -> {
                val input = CompoundInput()
                Resources.instance.addInput(name, input)
                data = input
            }
            sceneDirectoryP -> {
                val dir = File(Resources.instance.sceneDirectory, nameP.value)
                dir.mkdir()
                Resources.instance.fireAdded(dir, nameP.value)
            }
            sceneP -> {
                val dir = sceneP.value!!
                val file = File(dir, "${name}.scene")
                val newScene = SceneResource()
                var layoutName: String? = "default"
                if (Resources.instance.optionalLayout(layoutName!!) == null) {
                    layoutName = Resources.instance.layouts().keys.firstOrNull()
                    if (layoutName == null) {
                        throw ParameterException(sceneP, "The resources have no layouts. Create a Layout before creating a Scene.")
                    }
                }
                newScene.layoutName = layoutName
                JsonScene(newScene).save(file)
                Resources.instance.fireAdded(file, nameP.value)
            }
            fontP -> {

                val fontResource = FontResource()
                fontP.update(fontResource)
                Resources.instance.addFontResource(nameP.value, fontResource)
                data = fontResource
            }
        }

        if (data != null) {
            MainWindow.instance.openTab(nameP.value, data)
        }
    }
}


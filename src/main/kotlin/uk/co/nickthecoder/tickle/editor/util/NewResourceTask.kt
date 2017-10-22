package uk.co.nickthecoder.tickle.editor.util

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.*
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.util.JsonScene
import java.io.File


class NewResourceTask(type: ResourceType = ResourceType.ANY, defaultName: String = "") : AbstractTask() {

    val textureP = FileParameter("textureFile", label = "File", value = File(Resources.instance.file.parentFile, "images").absoluteFile)

    val poseP = createTextureParameter()

    val costumePoseP = createPoseParameter("pose", required = false)
    val costumeFontP = createFontParameter("font")
    val costumeP = OneOfParameter("newCostume", label = "From", choiceLabel = "From")
            .addParameters("Pose" to costumePoseP, "Font" to costumeFontP)
            .asHorizontal(LabelPosition.NONE)

    val costumeGroupP = InformationParameter("costumeGroup", information = "")

    val layoutP = InformationParameter("layout", information = "")

    val inputP = InformationParameter("input", information = "")

    val fontP = FontParameter("font")

    val sceneDirectoryP = InformationParameter("sceneDirectory", information = "")

    val sceneP = FileParameter("scene", label = "Directory", expectFile = false, value = Resources.instance.sceneDirectory.absoluteFile)

    val resourceTypeP = OneOfParameter("resourceType", label = "Resource", choiceLabel = "Type")


    val nameP = StringParameter("name", label = "${type.label} Name", value = defaultName)

    override val taskD = TaskDescription("newResource", label = "New ${type.label}", height = if (type == ResourceType.ANY) 300 else null)
            .addParameters(resourceTypeP, nameP)

    override val taskRunner = UnthreadedTaskRunner(this)

    constructor(pose: Pose, defaultName: String = "") : this(ResourceType.COSTUME, defaultName) {
        costumeP.value = costumePoseP
        costumePoseP.value = pose
    }

    constructor(fontResource: FontResource, defaultName: String = "") : this(ResourceType.COSTUME, defaultName) {
        costumeP.value = costumeFontP
        costumeFontP.value = fontResource
    }


    init {

        val parameter = when (type) {
            ResourceType.POSE -> poseP
            ResourceType.COSTUME -> costumeP
            ResourceType.COSTUME_GROUP -> costumeGroupP
            ResourceType.LAYOUT -> layoutP
            ResourceType.INPUT -> inputP
            ResourceType.FONT -> fontP
            ResourceType.SCENE_DIRECTORY -> sceneDirectoryP
            ResourceType.SCENE -> sceneP
            else -> null

        }
        if (parameter == null) {
            resourceTypeP.addParameters(
                    "Texture" to textureP,
                    "Pose" to poseP, "Font" to fontP,
                    "Costume" to costumeP,
                    "Costume Group" to costumeGroupP,
                    "Layout" to layoutP,
                    "Input" to inputP,
                    "Scene Directory" to sceneDirectoryP,
                    "Scene" to sceneP)
        } else {
            resourceTypeP.hidden = true
            resourceTypeP.value = parameter
            if (parameter !is InformationParameter) {
                taskD.addParameters(parameter)
            }
        }
    }

    override fun customCheck() {
        val resourceType = when (resourceTypeP.value) {
            textureP -> Resources.instance.textures
            poseP -> Resources.instance.poses
            costumeP -> Resources.instance.costumes
            costumeGroupP -> Resources.instance.costumeGroups
            layoutP -> Resources.instance.layouts
            inputP -> Resources.instance.inputs
            fontP -> Resources.instance.fontResources
            sceneDirectoryP -> null
            sceneP -> null
            else -> null
        }

        if (resourceType != null) {
            if (resourceType.find(nameP.value) != null) {
                throw ParameterException(nameP, "Already exists")
            }
        }
        // TODO Do similar tests for sceneDirectory name and scene name.
    }

    override fun run() {

        val name = nameP.value
        var data: Any? = null

        when (resourceTypeP.value) {
            textureP -> {
                data = Resources.instance.textures.add(name, Texture.create(textureP.value!!))
            }
            poseP -> {
                val pose = Pose(poseP.value!!)
                Resources.instance.poses.add(name, pose)
                data = pose
            }
            costumeP -> {
                val costume = Costume()
                if (costumeP.value == costumePoseP) {
                    costumePoseP.value?.let {
                        costume.addPose("default", it)
                    }
                } else if (costumeP.value == costumeFontP) {
                    costumeFontP.value?.let { font ->
                        val textStyle = TextStyle(font, HAlignment.LEFT, VAlignment.BOTTOM, Color.white())
                        costume.addTextStyle("default", textStyle)
                    }
                }
                Resources.instance.costumes.add(name, costume)
                data = costume
            }
            costumeGroupP -> {
                val costumeGroup = CostumeGroup(Resources.instance)
                Resources.instance.costumeGroups.add(name, costumeGroup)
                data = costumeGroup
            }
            layoutP -> {
                val layout = Layout()
                layout.layoutStages["main"] = LayoutStage()
                layout.layoutViews["main"] = LayoutView(stageName = "main")
                Resources.instance.layouts.add(name, layout)
                data = layout
            }
            inputP -> {
                val input = CompoundInput()
                Resources.instance.inputs.add(name, input)
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
                if (Resources.instance.layouts.find(layoutName!!) == null) {
                    layoutName = Resources.instance.layouts.items().keys.firstOrNull()
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
                Resources.instance.fontResources.add(nameP.value, fontResource)
                data = fontResource
            }
        }

        if (data != null) {
            MainWindow.instance.openTab(nameP.value, data)
        }
    }

    fun prompt() {
        val taskPrompter = TaskPrompter(this)
        taskPrompter.placeOnStage(Stage())
    }

}

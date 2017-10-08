package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.demo.NoProducer
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.stage.FlexHAlignment
import uk.co.nickthecoder.tickle.stage.FlexVAlignment
import java.io.*

class JsonResources {

    var resources: Resources

    constructor(file: File) {
        this.resources = Resources()
        load(file)
    }

    constructor(resources: Resources) {
        this.resources = resources
    }


    fun save(file: File) {

        resources.file = file.absoluteFile

        val jroot = JsonObject()
        jroot.add("info", saveInfo())
        addArray(jroot, "layouts", saveLayouts())
        addArray(jroot, "textures", saveTextures())
        addArray(jroot, "poses", savePoses())
        addArray(jroot, "costumes", saveCostumes())
        addArray(jroot, "inputs", saveInputs())

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }
    }

    fun addArray(obj: JsonObject, name: String, array: JsonArray) {
        if (!array.isEmpty) {
            obj.add(name, array)
        }
    }

    fun load(file: File) {
        resources.file = file
        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

        val jinfo = jroot.get("info")
        val jlayouts = jroot.get("layouts")
        val jtextures = jroot.get("textures")
        val jposes = jroot.get("poses")
        val jcostumes = jroot.get("costumes")
        val jinputs = jroot.get("inputs")

        if (jinfo is JsonObject) {
            loadInfo(jinfo)
        }
        if (jtextures is JsonArray) {
            loadTextures(jtextures)
        }
        if (jlayouts is JsonArray) {
            loadLayouts(jlayouts)
        }
        if (jposes is JsonArray) {
            loadPoses(jposes)
        }
        if (jcostumes is JsonArray) {
            loadCostumes(jcostumes)
        }
        if (jinputs is JsonArray) {
            loadInputs(jinputs)
        }

    }

    // INFO

    fun saveInfo(): JsonObject {
        val jinfo = JsonObject()
        with(resources.gameInfo) {
            jinfo.add("title", title)
            jinfo.add("width", width)
            jinfo.add("height", height)
            jinfo.add("resizable", resizable)

            jinfo.add("initialScene", resources.scenePathToString(initialScenePath))
            jinfo.add("testScene", resources.scenePathToString(testScenePath))

            jinfo.add("producer", producerString)
            val jpackages = JsonArray()
            packages.forEach {
                jpackages.add(it)
            }
            jinfo.add("packages", jpackages)
            return jinfo
        }
    }

    fun loadInfo(jinfo: JsonObject) {
        with(resources.gameInfo) {
            title = jinfo.getString("title", "Tickle Game")
            width = jinfo.getInt("width", 800)
            height = jinfo.getInt("height", 600)
            resizable = jinfo.getBoolean("resizable", true)

            initialScenePath = resources.scenePathFromString(jinfo.getString("initialScene", "splash"))
            testScenePath = resources.scenePathFromString(jinfo.getString("testScene", "splash"))

            producerString = jinfo.getString("producer", NoProducer::javaClass.name)
            jinfo.get("packages")?.let {
                val newPackages = it.asArray()
                packages.clear()
                newPackages.forEach {
                    packages.add(it.asString())
                }
            }

            // println("Loaded info : $title : $width x $height Resize? $resizable. Game=$producerString")
        }
    }

    // LAYOUTS

    fun saveLayouts(): JsonArray {
        val jlayouts = JsonArray()
        resources.layouts().forEach { name, layout ->
            val jlayout = JsonObject()
            jlayouts.add(jlayout)
            jlayout.add("name", name)

            val jstages = JsonArray()
            jlayout.add("stages", jstages)

            layout.layoutStages.forEach { stageName, layoutStage ->
                val jstage = JsonObject()
                jstages.add(jstage)

                jstage.add("name", stageName)
                jstage.add("stage", layoutStage.stageString)
            }

            val jviews = JsonArray()
            jlayout.add("views", jviews)

            layout.layoutViews.forEach { viewName, layoutView ->
                val jview = JsonObject()
                jviews.add(jview)

                jview.add("name", viewName)
                jview.add("view", layoutView.viewString)
                if (layoutView.stageName.isNotBlank()) {
                    jview.add("stage", layoutView.stageName)
                }

                with(layoutView.position) {
                    jview.add("hAlignment", hAlignment.name)
                    if (hAlignment == FlexHAlignment.MIDDLE) {
                        jview.add("hPosition", hPosition)
                    } else {
                        jview.add("leftRightMargin", leftRightMargin)
                    }
                    width?.let { jview.add("width", it) }
                    widthRatio?.let { jview.add("widthRatio", it) }

                    jview.add("vAlignment", vAlignment.name)
                    if (vAlignment == FlexVAlignment.MIDDLE) {
                        jview.add("vPosition", vPosition)
                    } else {
                        jview.add("topBottomMargin", topBottomMargin)
                    }
                    height?.let { jview.add("height", it) }
                    heightRatio?.let { jview.add("heightRatio", it) }
                }
            }
        }
        // println("Created jlayouts $jlayouts")
        return jlayouts
    }

    fun loadLayouts(jlayouts: JsonArray) {
        jlayouts.forEach { jele ->
            val jlayout = jele.asObject()
            val name = jlayout.get("name").asString()
            val layout = Layout()

            jlayout.get("stages")?.let {
                val jstages = it.asArray()
                jstages.forEach {
                    val jstage = it.asObject()
                    val layoutStage = LayoutStage()
                    val stageName = jstage.get("name").asString()
                    layoutStage.stageString = jstage.get("stage").asString()
                    layout.layoutStages[stageName] = layoutStage
                }
            }

            jlayout.get("views")?.let {
                val jviews = it.asArray()
                jviews.forEach {
                    val jview = it.asObject()
                    val layoutView = LayoutView()
                    val viewName = jview.get("name").asString()
                    layoutView.viewString = jview.get("view").asString()
                    layoutView.stageName = jview.getString("stage", "")

                    // X
                    val hAlignmentString = jview.getString("hAlignment", FlexHAlignment.LEFT.name)
                    layoutView.position.hAlignment = FlexHAlignment.valueOf(hAlignmentString)
                    if (layoutView.position.hAlignment == FlexHAlignment.MIDDLE) {
                        layoutView.position.hPosition = jview.get("hPosition").asDouble()
                    } else {
                        layoutView.position.leftRightMargin = jview.getInt("leftRightMargin", 0)
                    }
                    layoutView.position.width = jview.get("width")?.asInt()
                    layoutView.position.widthRatio = jview.get("widthRatio")?.asDouble()

                    // Y
                    val vAlignmentString = jview.getString("vAlignment", FlexVAlignment.BOTTOM.name)
                    layoutView.position.vAlignment = FlexVAlignment.valueOf(vAlignmentString)
                    if (layoutView.position.vAlignment == FlexVAlignment.MIDDLE) {
                        layoutView.position.vPosition = jview.get("vPosition").asDouble()
                    } else {
                        layoutView.position.topBottomMargin = jview.getInt("topBottomMargin", 0)
                    }
                    layoutView.position.height = jview.get("height")?.asInt()
                    layoutView.position.heightRatio = jview.get("heightRatio")?.asDouble()

                    layout.layoutViews[viewName] = layoutView
                }
            }
            resources.addLayout(name, layout)

        }
    }

// TEXTURES

    fun saveTextures(): JsonArray {
        val jtextures = JsonArray()
        resources.textures().forEach { name, texture ->
            texture.file?.let { file ->
                val jtexture = JsonObject()
                jtexture.add("name", name)
                jtexture.add("file", toPath(file))
                jtextures.add(jtexture)
            }
        }
        return jtextures
    }

    fun loadTextures(jtextures: JsonArray) {
        jtextures.forEach { jele ->
            val jtexture = jele.asObject()
            val name = jtexture.get("name").asString()
            val file = fromPath(jtexture.get("file").asString())
            resources.addTexture(name, file)

            // println("Loaded texture $name : $file")
        }
    }

// POSES

    fun savePoses(): JsonArray {
        val jposes = JsonArray()
        resources.poses().forEach { name, pose ->
            resources.findTextureName(pose.texture)?.let { textureName ->
                val jpose = JsonObject()
                jpose.add("name", name)
                jpose.add("texture", textureName)
                jpose.add("left", pose.rect.left)
                jpose.add("bottom", pose.rect.bottom)
                jpose.add("right", pose.rect.right)
                jpose.add("top", pose.rect.top)
                jpose.add("offsetX", pose.offsetX)
                jpose.add("offsetY", pose.offsetY)
                jpose.add("direction", pose.direction.degrees)

                jposes.add(jpose)
            }
        }
        return jposes

    }

    fun loadPoses(jposes: JsonArray) {
        jposes.forEach { jele ->
            val jpose = jele.asObject()
            val name = jpose.get("name").asString()
            val textureName = jpose.get("texture").asString()
            val pose = Pose(resources.texture(textureName))

            pose.rect.left = jpose.get("left").asInt()
            pose.rect.bottom = jpose.get("bottom").asInt()
            pose.rect.right = jpose.get("right").asInt()
            pose.rect.top = jpose.get("top").asInt()

            pose.offsetX = jpose.get("offsetX").asDouble()
            pose.offsetY = jpose.get("offsetY").asDouble()

            pose.direction.degrees = jpose.get("direction").asDouble()
            pose.updateRectd()

            resources.addPose(name, pose)
            //println("Loaded pose $name : ${pose}")
        }
    }
// COSTUMES

    fun saveCostumes(): JsonArray {
        val jcostumes = JsonArray()
        resources.costumes().forEach { name, costume ->
            val jcostume = JsonObject()
            jcostume.add("name", name)
            jcostume.add("role", costume.roleString)
            jcostume.add("canRotate", costume.canRotate)

            val jevents = JsonArray()
            jcostume.add("events", jevents)
            costume.events.forEach { eventName, event ->
                val jevent = JsonObject()
                jevents.add(jevent)
                jevent.add("name", eventName)

                if (event.poses.isNotEmpty()) {
                    val jposes = JsonArray()
                    event.poses.forEach { pose ->
                        resources.findPoseName(pose)?.let { poseName ->
                            jposes.add(poseName)
                        }
                    }
                    jevent.add("poses", jposes)
                }
            }
            JsonUtil.saveAttributes(jcostume, costume.attributes)

            jcostumes.add(jcostume)
        }

        return jcostumes
    }

    fun loadCostumes(jcostumes: JsonArray) {
        jcostumes.forEach {
            val jcostume = it.asObject()
            val name = jcostume.get("name").asString()
            val costume = Costume()
            costume.roleString = jcostume.getString("role", "")
            costume.canRotate = jcostume.getBoolean("canRotate", false)

            jcostume.get("events")?.let {
                val jevents = it.asArray()
                jevents.forEach {
                    val jevent = it.asObject()
                    val eventName = jevent.get("name").asString()
                    val event = CostumeEvent()
                    jevent.get("poses")?.let {
                        val jposes = it.asArray()
                        jposes.forEach {
                            val poseName = it.asString()
                            val pose = resources.optionalPose(poseName)
                            pose?.let { event.poses.add(it) }
                        }
                    }
                    costume.events[eventName] = event
                }
            }

            JsonUtil.loadAttributes(jcostume, costume.attributes)

            resources.addCostume(name, costume)
            // println("Loaded costume $name : ${costume}")
        }
    }

// INPUTS

    fun saveInputs(): JsonArray {
        val jinputs = JsonArray()
        resources.inputs().forEach { name, input ->
            val jinput = JsonObject()
            jinput.add("name", name)

            val jkeys = JsonArray()
            addKeyInputs(input, jkeys)
            if (!jkeys.isEmpty) {
                jinput.add("keys", jkeys)
            }

            val jmouseButtons = JsonArray()
            addMouseInputs(input, jmouseButtons)
            if (!jmouseButtons.isEmpty) {
                jinput.add("mouse", jmouseButtons)
            }

            val jjoystickButtons = JsonArray()
            addJoystickButtonInputs(input, jjoystickButtons)
            if (!jjoystickButtons.isEmpty) {
                jinput.add("joystick", jjoystickButtons)
            }

            val jjoystickAxis = JsonArray()
            addJoystickAxisInputs(input, jjoystickAxis)
            if (!jjoystickAxis.isEmpty) {
                jinput.add("joystickAxis", jjoystickAxis)
            }

            jinputs.add(jinput)
        }
        return jinputs
    }

    fun addKeyInputs(input: Input, toArray: JsonArray) {

        if (input is KeyInput) {
            val jkey = JsonObject()
            jkey.add("key", input.key.label)
            jkey.add("state", input.state.name)
            toArray.add(jkey)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addKeyInputs(it, toArray)
            }
        }
    }

    fun addMouseInputs(input: Input, toArray: JsonArray) {

        if (input is MouseInput) {
            val jmouse = JsonObject()
            jmouse.add("button", input.mouseButton)
            jmouse.add("state", input.state.name)
            toArray.add(jmouse)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addMouseInputs(it, toArray)
            }
        }
    }

    fun addJoystickButtonInputs(input: Input, toArray: JsonArray) {

        if (input is JoystickButtonInput) {
            val jjoystick = JsonObject()
            jjoystick.add("joystickID", input.joystickID)
            jjoystick.add("button", input.button.name)
            toArray.add(jjoystick)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addJoystickButtonInputs(it, toArray)
            }
        }
    }

    fun addJoystickAxisInputs(input: Input, toArray: JsonArray) {

        if (input is JoystickAxisInput) {
            val jjoystick = JsonObject()
            jjoystick.add("joystickID", input.joystickID)
            jjoystick.add("axis", input.axis.name)
            jjoystick.add("positive", input.positive)
            jjoystick.add("threshold", input.threshold)
            toArray.add(jjoystick)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addJoystickAxisInputs(it, toArray)
            }
        }
    }

    fun loadInputs(jinputs: JsonArray) {
        jinputs.forEach { jele ->
            val jinput = jele.asObject()
            val name = jinput.get("name").asString()
            val input = CompoundInput()

            jinput.get("keys")?.let {
                val jkeys = it.asArray()
                jkeys.forEach {
                    val jkey = it.asObject()
                    val key = jkey.get("key").asString()
                    val stateString = jkey.getString("state", "PRESSED")
                    val state = ButtonState.valueOf(stateString)
                    input.add(KeyInput(Key.forLabel(key), state))
                }
            }

            jinput.get("mouse")?.let {
                val jmice = it.asArray()
                jmice.forEach {
                    val jmouse = it.asObject()
                    val button = jmouse.getInt("button", 1)
                    val stateString = jmouse.getString("state", "PRESSED")
                    val state = ButtonState.valueOf(stateString)
                    input.add(MouseInput(button, state))
                }
            }

            jinput.get("joystick")?.let {
                val jbuttons = it.asArray()
                jbuttons.forEach {
                    val jbutton = it.asObject()
                    val joystickID = jbutton.getInt("joystickID", 0)
                    val buttonString = jbutton.getString("button", JoystickButton.A.name)
                    val button = JoystickButton.valueOf(buttonString)
                    input.add(JoystickButtonInput(joystickID, button))
                }
            }

            jinput.get("joystickAxis")?.let {
                val jaxes = it.asArray()
                jaxes.forEach {
                    val jaxis = it.asObject()
                    val joystickID = jaxis.getInt("joystickID", 0)
                    val axisString = jaxis.getString("axis", JoystickAxis.LEFT_X.name)
                    val positive = jaxis.getBoolean("positive", true)
                    val threshold = jaxis.getDouble("threshold", 0.5)
                    val axis = JoystickAxis.valueOf(axisString)
                    input.add(JoystickAxisInput(joystickID, axis, positive, threshold))
                }
            }

            resources.addInput(name, input)
            // println("Loaded input $name : $input")
        }
    }

    // Utility methods

    fun toPath(file: File): String {
        try {
            return file.absoluteFile.toRelativeString(resources.resourceDirectory)
        } catch(e: Exception) {
            return file.absolutePath
        }
    }

    fun fromPath(path: String): File {
        return resources.resourceDirectory.resolve(path).absoluteFile
    }

}

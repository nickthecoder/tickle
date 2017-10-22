package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.graphics.*
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.stage.FlexHAlignment
import uk.co.nickthecoder.tickle.stage.FlexVAlignment
import java.io.*

class JsonResources {

    var resources: Resources

    /**
     * WHile loading the costumes, one costume event may depend upon a costume that hasn't been loaded yet.
     * So we store the events in a list, and process them once all costumes have been loaded.
     */
    val costumeEvents = mutableListOf<CostumeEventData>()

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
        jroot.add("preferences", savePreferences())
        addArray(jroot, "layouts", saveLayouts())
        addArray(jroot, "textures", saveTextures())
        addArray(jroot, "fonts", saveFonts())
        addArray(jroot, "poses", savePoses())
        addArray(jroot, "costumeGroups", saveCostumeGroups())
        addArray(jroot, "costumes", saveCostumes(resources.costumes, false))
        addArray(jroot, "inputs", saveInputs())

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, resources.preferences.outputFormat.writerConfig)
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
        val jpreferences = jroot.get("preferences")
        val jlayouts = jroot.get("layouts")
        val jtextures = jroot.get("textures")
        val jfonts = jroot.get("fonts")
        val jposes = jroot.get("poses")
        val jcostumeGroups = jroot.get("costumeGroups")
        val jcostumes = jroot.get("costumes")
        val jinputs = jroot.get("inputs")

        if (jinfo is JsonObject) {
            loadInfo(jinfo)
        }
        if (jpreferences is JsonObject) {
            loadPreferences(jpreferences)
        }
        if (jlayouts is JsonArray) {
            loadLayouts(jlayouts)
        }
        if (jtextures is JsonArray) {
            loadTextures(jtextures)
        }
        if (jfonts is JsonArray) {
            loadFonts(jfonts)
        }
        if (jposes is JsonArray) {
            loadPoses(jposes)
        }
        if (jcostumes is JsonArray) {
            loadCostumes(jcostumes, resources.costumes)
        }
        if (jcostumeGroups is JsonArray) {
            loadCostumeGroups(jcostumeGroups)
        }
        if (jinputs is JsonArray) {
            loadInputs(jinputs)
        }

        // Now all costume have been loaded, lets add the costume events.
        costumeEvents.forEach { data ->
            data.costumeEvent.costumes.add(resources.costumes.find(data.costumeName)!!)
        }
    }

    // EDITOR PREFERENCES

    fun savePreferences(): JsonObject {
        val jpreferences = JsonObject()
        with(resources.preferences) {

            jpreferences.add("outputFormat", outputFormat.name)

            val jpackages = JsonArray()
            packages.forEach {
                jpackages.add(it)
            }
            jpreferences.add("packages", jpackages)
            jpreferences.add("treeThumbnailSize", treeThumnailSize)
            jpreferences.add("costumePickerThumbnailSize", costumePickerThumbnailSize)

            return jpreferences
        }
    }

    fun loadPreferences(jpreferences: JsonObject) {
        with(resources.preferences) {

            jpreferences.get("packages")?.let {
                val newPackages = it.asArray()
                packages.clear()
                newPackages.forEach {
                    packages.add(it.asString())
                }
            }
            ClassLister.packages(packages)

            outputFormat = EditorPreferences.JsonFormat.valueOf(jpreferences.getString("outputFormat", "PRETTY"))
            treeThumnailSize = jpreferences.getInt("treeThumbnailSize", 24)
            costumePickerThumbnailSize = jpreferences.getInt("costumePickerThumbnailSize", 40)

            // println("Loaded preferences : ${resources.preferences}")
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

            jinfo.add("initialScene", resources.sceneFileToPath(initialScenePath))
            jinfo.add("testScene", resources.sceneFileToPath(testScenePath))

            jinfo.add("producer", producerString)

            return jinfo
        }
    }

    fun loadInfo(jinfo: JsonObject) {
        with(resources.gameInfo) {
            title = jinfo.getString("title", "Tickle Game")
            width = jinfo.getInt("width", 800)
            height = jinfo.getInt("height", 600)
            resizable = jinfo.getBoolean("resizable", true)

            initialScenePath = resources.scenePathToFile(jinfo.getString("initialScene", "splash"))
            testScenePath = resources.scenePathToFile(jinfo.getString("testScene", "splash"))
            producerString = jinfo.getString("producer", NoProducer::javaClass.name)

            // println("Loaded info : $title : $width x $height Resize? $resizable. Game=$producerString")
        }
    }

    // LAYOUTS

    fun saveLayouts(): JsonArray {
        val jlayouts = JsonArray()
        resources.layouts.items().forEach { name, layout ->
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
                jstage.add("constraint", layoutStage.stageConstraintString)
                JsonUtil.saveAttributes(jstage, layoutStage.constraintAttributes, "constraintAttributes")
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
                jview.add("zOrder", layoutView.zOrder)

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
                    layoutStage.stageConstraintString = jstage.getString("constraint", NoStageConstraint::class.java.name)
                    JsonUtil.loadAttributes(jstage, layoutStage.constraintAttributes, "constraintAttributes")

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
                    layoutView.zOrder = jview.getInt("zOrder", 50)

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
            resources.layouts.add(name, layout)

        }
    }

    // TEXTURES

    fun saveTextures(): JsonArray {
        val jtextures = JsonArray()
        resources.textures.items().forEach { name, texture ->
            texture.file?.let { file ->
                val jtexture = JsonObject()
                jtexture.add("name", name)
                jtexture.add("file", resources.toPath(file))
                jtextures.add(jtexture)
            }
        }
        return jtextures
    }

    fun loadTextures(jtextures: JsonArray) {
        jtextures.forEach { jele ->
            val jtexture = jele.asObject()
            val name = jtexture.get("name").asString()
            val file = resources.fromPath(jtexture.get("file").asString())
            resources.textures.add(name, Texture.create(file))

            // println("Loaded texture $name : $file")
        }
    }

    // POSES

    fun savePoses(): JsonArray {
        val jposes = JsonArray()
        resources.poses.items().forEach { name, pose ->
            resources.textures.findName(pose.texture)?.let { textureName ->
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
            val pose = Pose(resources.textures.find(textureName)!!)

            pose.rect.left = jpose.get("left").asInt()
            pose.rect.bottom = jpose.get("bottom").asInt()
            pose.rect.right = jpose.get("right").asInt()
            pose.rect.top = jpose.get("top").asInt()

            pose.offsetX = jpose.get("offsetX").asDouble()
            pose.offsetY = jpose.get("offsetY").asDouble()

            pose.direction.degrees = jpose.get("direction").asDouble()
            pose.updateRectd()

            resources.poses.add(name, pose)
            //println("Loaded pose $name : ${pose}")
        }
    }

    // Costume Groups

    fun saveCostumeGroups(): JsonArray {
        val jgroups = JsonArray()

        resources.costumeGroups.items().forEach { name, group ->
            val jgroup = JsonObject()
            jgroup.add("name", name)
            jgroup.add("costumes", saveCostumes(group, true))

            jgroup.add("showInSceneEditor", group.showInSceneEditor)
            jgroups.add(jgroup)
        }
        return jgroups
    }

    fun loadCostumeGroups(jgroups: JsonArray) {

        jgroups.forEach {
            val jgroup = it.asObject()
            val group = CostumeGroup(resources)
            val groupName = jgroup.get("name").asString()
            group.showInSceneEditor = jgroup.getBoolean("showInSceneEditor", true)

            val jcostumes = jgroup.get("costumes").asArray()
            loadCostumes(jcostumes, group)

            group.items().forEach { costumeName, costume ->
                resources.costumes.add(costumeName, costume)
            }
            resources.costumeGroups.add(groupName, group)
        }
    }

    // COSTUMES

    fun saveCostumes(costumes: ResourceMap<Costume>, all: Boolean): JsonArray {
        val jcostumes = JsonArray()

        costumes.items().forEach { name, costume ->

            if (all || resources.findCostumeGroup(name) == null) {

                val jcostume = JsonObject()
                jcostume.add("name", name)
                jcostume.add("role", costume.roleString)
                jcostume.add("canRotate", costume.canRotate)
                jcostume.add("zOrder", costume.zOrder)
                jcostume.add("initialEvent", costume.initialEventName)
                jcostume.add("showInSceneEditor", costume.showInSceneEditor)

                val jevents = JsonArray()
                jcostume.add("events", jevents)
                costume.events.forEach { eventName, event ->
                    val jevent = JsonObject()
                    jevents.add(jevent)
                    jevent.add("name", eventName)

                    if (event.poses.isNotEmpty()) {
                        val jposes = JsonArray()
                        event.poses.forEach { pose ->
                            resources.poses.findName(pose)?.let { poseName ->
                                jposes.add(poseName)
                            }
                        }
                        jevent.add("poses", jposes)
                    }

                    if (event.costumes.isNotEmpty()) {
                        val jcos = JsonArray()
                        event.costumes.forEach { cos ->
                            resources.costumes.findName(cos)?.let { cosName ->
                                jcos.add(cosName)
                            }
                        }
                        jevent.add("costumes", jcos)
                    }

                    if (event.textStyles.isNotEmpty()) {
                        val jtextStyles = JsonArray()
                        event.textStyles.forEach { textStyle ->
                            val jtextStyle = JsonObject()
                            jtextStyles.add(jtextStyle)
                            jtextStyle.add("font", resources.fontResources.findName(textStyle.fontResource))
                            jtextStyle.add("halign", textStyle.halignment.name)
                            jtextStyle.add("valign", textStyle.valignment.name)
                            jtextStyle.add("color", textStyle.color.toHashRGBA())
                            if (textStyle.fontResource.outlineFontTexture != null) {
                                textStyle.outlineColor?.let {
                                    jtextStyle.add("outlineColor", it.toHashRGBA())
                                }
                            }
                        }
                        jevent.add("textStyles", jtextStyles)
                    }
                    if (event.strings.isNotEmpty()) {
                        val jstrings = JsonArray()
                        event.strings.forEach { str ->
                            jstrings.add(str)
                        }
                        jevent.add("strings", jstrings)
                    }
                }
                JsonUtil.saveAttributes(jcostume, costume.attributes)

                jcostumes.add(jcostume)
            }
        }

        return jcostumes
    }

    data class CostumeEventData(val costumeEvent: CostumeEvent, val costumeName: String)

    fun loadCostumes(jcostumes: JsonArray, group: ResourceMap<Costume>) {

        jcostumes.forEach {
            val jcostume = it.asObject()
            val name = jcostume.get("name").asString()
            val costume = Costume()
            costume.roleString = jcostume.getString("role", "")
            costume.canRotate = jcostume.getBoolean("canRotate", false)
            costume.zOrder = jcostume.getDouble("zOrder", 0.0)
            costume.initialEventName = jcostume.getString("initialEvent", "default")
            costume.showInSceneEditor = jcostume.getBoolean("showInSceneEditor", true)

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
                            resources.poses.find(poseName)?.let { event.poses.add(it) }
                        }
                    }

                    jevent.get("costumes")?.let {
                        val jcoses = it.asArray()
                        jcoses.forEach {
                            val costumeName = it.asString()
                            // We cannot add the costume event at this point, because the costume may not have been loaded yet
                            // So instead, save the details, and process it afterwards.
                            costumeEvents.add(CostumeEventData(event, costumeName))
                        }
                    }

                    jevent.get("textStyles")?.let {
                        val jtextStyles = it.asArray()
                        jtextStyles.forEach {
                            val jtextStyle = it.asObject()
                            val fontResource = resources.fontResources.find(jtextStyle.get("font").asString())!!
                            val halign = HAlignment.valueOf(jtextStyle.getString("halign", HAlignment.LEFT.name))
                            val valign = VAlignment.valueOf(jtextStyle.getString("valign", VAlignment.BASELINE.name))
                            val color = Color.fromString(jtextStyle.getString("color", "#FFFFFF"))
                            val textStyle = TextStyle(fontResource, halign, valign, color)
                            if (textStyle.fontResource.outlineFontTexture != null) {
                                val outlineColor = Color.fromString(jtextStyle.getString("outlineColor", "#000000"))
                                textStyle.outlineColor = outlineColor
                            }
                            event.textStyles.add(textStyle)
                        }
                    }

                    jevent.get("strings")?.let {
                        val jstrings = it.asArray()
                        jstrings.forEach {
                            event.strings.add(it.asString())
                        }
                    }

                    costume.events[eventName] = event
                }
            }

            JsonUtil.loadAttributes(jcostume, costume.attributes)

            group.add(name, costume)
            // println("Loaded costume $name : ${costume}")
        }

    }

    // INPUTS

    fun saveInputs(): JsonArray {
        val jinputs = JsonArray()
        resources.inputs.items().forEach { name, input ->
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

            resources.inputs.add(name, input)
            // println("Loaded input $name : $input")
        }
    }


    // FONTS

    fun saveFonts(): JsonArray {
        val jfonts = JsonArray()
        resources.fontResources.items().forEach { name, fontResource ->
            val jfont = JsonObject()
            jfont.add("name", name)
            if (fontResource.file == null) {
                jfont.add("fontName", fontResource.fontName)
                jfont.add("style", fontResource.style.name)
            } else {
                jfont.add("file", resources.toPath(fontResource.file!!))
            }
            jfont.add("size", fontResource.size)
            jfont.add("xPadding", fontResource.xPadding)
            jfont.add("yPadding", fontResource.yPadding)
            jfonts.add(jfont)
        }

        return jfonts

    }

    fun loadFonts(jfonts: JsonArray) {
        jfonts.forEach { jele ->
            val jfont = jele.asObject()
            val name = jfont.get("name").asString()
            if (jfont.get("fontName") != null) {
                val fontResource = FontResource()

                val fontPath = jfont.getString("file", "")

                if (fontPath.isBlank()) {
                    fontResource.fontName = jfont.get("fontName").asString()
                    val styleString = jfont.getString("style", "PLAIN")
                    fontResource.style = FontResource.FontStyle.valueOf(styleString)
                } else {
                    fontResource.file = resources.fromPath(fontPath)
                }

                fontResource.size = jfont.getDouble("size", 22.0)
                fontResource.xPadding = jfont.getInt("xPadding", 1)
                fontResource.yPadding = jfont.getInt("yPadding", 1)

                val pngFile = File(resources.texturesDirectory, "$name.png")
                val outlinePngFile = File(resources.texturesDirectory, "$name-outline.png")
                val metricsFile = File(resources.texturesDirectory, "$name.metrics")

                if (pngFile.exists() && metricsFile.exists()) {
                    val texture = Texture.create(pngFile)
                    val fontTexture = loadFontMetrics(metricsFile, texture)
                    fontResource.fontTexture = fontTexture

                    if (outlinePngFile.exists()) {
                        val outlineTexture = Texture.create(outlinePngFile)
                        fontResource.outlineFontTexture = FontTexture(copyGlyphs(outlineTexture, fontTexture.glyphs), fontTexture.lineHeight,
                                leading = fontTexture.leading, ascent = fontTexture.ascent, descent = fontTexture.descent)
                    }
                }

                resources.fontResources.add(name, fontResource)
            }
            //println("Loaded pose $name : ${pose}")
        }
    }

    companion object {

        fun copyGlyphs(texture: Texture, glyphs: Map<Char, Glyph>): Map<Char, Glyph> {
            val result = mutableMapOf<Char, Glyph>()
            glyphs.forEach { c, glyph ->
                val pose = Pose(texture, glyph.pose.rect)
                pose.offsetX = glyph.pose.offsetX
                pose.offsetY = glyph.pose.offsetY
                result[c] = Glyph(pose, glyph.advance)
            }
            return result
        }

        fun saveFontMetrics(file: File, fontResource: FontResource) {
            val fontTexture = fontResource.fontTexture
            val jroot = JsonObject()
            jroot.add("lineHeight", fontTexture.lineHeight)
            jroot.add("leading", fontTexture.leading)
            jroot.add("ascent", fontTexture.ascent)
            jroot.add("descent", fontTexture.descent)
            jroot.add("xPadding", fontResource.xPadding)
            jroot.add("yPadding", fontResource.yPadding)
            val jglyphs = JsonArray()
            jroot.add("glyphs", jglyphs)
            fontTexture.glyphs.forEach { c, data ->
                val jglyph = JsonObject()
                jglyph.add("c", c.toString())
                jglyph.add("left", data.pose.rect.left)
                jglyph.add("top", data.pose.rect.top)
                jglyph.add("right", data.pose.rect.right)
                jglyph.add("bottom", data.pose.rect.bottom)
                jglyph.add("advance", data.advance)

                jglyphs.add(jglyph)
            }

            BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
                jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
            }
        }

        fun loadFontMetrics(file: File, texture: Texture): FontTexture {
            val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

            val lineHeight = jroot.get("lineHeight").asDouble()
            val leading = jroot.get("leading").asDouble()
            val ascent = jroot.get("ascent").asDouble()
            val descent = jroot.get("descent").asDouble()
            val xPadding = jroot.getDouble("xPadding", 1.0)
            val yPadding = jroot.getDouble("yPadding", 1.0)

            val glyphs = mutableMapOf<Char, Glyph>()

            val jglyphs = jroot.get("glyphs").asArray()
            jglyphs.forEach {
                val jglyph = it.asObject()
                val c = jglyph.get("c").asString().first()
                val left = jglyph.get("left").asInt()
                val top = jglyph.get("top").asInt()
                val right = jglyph.get("right").asInt()
                val bottom = jglyph.get("bottom").asInt()
                val advance = jglyph.get("advance").asDouble()
                val rect = YDownRect(left, top, right, bottom)
                val pose = Pose(texture, rect)
                pose.offsetX = xPadding
                pose.offsetY = rect.height - yPadding
                val glyph = Glyph(pose, advance)
                glyphs[c] = (glyph)
            }

            return FontTexture(glyphs, lineHeight, leading = leading, ascent = ascent, descent = descent)
        }
    }
}

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
package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import org.jbox2d.dynamics.BodyType
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.graphics.*
import uk.co.nickthecoder.tickle.physics.*
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import uk.co.nickthecoder.tickle.sound.Sound
import uk.co.nickthecoder.tickle.stage.FlexHAlignment
import uk.co.nickthecoder.tickle.stage.FlexVAlignment
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

open class JsonResources {

    protected var resources: Resources

    /**
     * While loading the costumes, one costume event may depend upon a costume that hasn't been loaded yet.
     * So we store the events in a list, and process them once all costumes have been loaded.
     */
    protected val costumeEvents = mutableListOf<CostumeEventData>()

    /**
     * When a costume inherits events from another costume, store the costume names in this map, and then
     * update the costume once all of the costumes have been loaded.
     */
    protected val inheritedCostumeEvents = mutableMapOf<String, String>()

    constructor(file: File) {
        this.resources = Resources()
        this.resources.file = file
    }

    constructor(resources: Resources) {
        this.resources = resources
    }


    fun addArray(obj: JsonObject, name: String, array: JsonArray) {
        if (!array.isEmpty) {
            obj.add(name, array)
        }
    }

    fun loadGameInfo(): GameInfo {
        val jroot = Json.parse(InputStreamReader(FileInputStream(resources.file))).asObject()

        jroot.get("info")?.let {
            loadInfo(it as JsonObject)
        }
        return resources.gameInfo
    }

    fun loadResources(): Resources {
        val jroot = Json.parse(InputStreamReader(FileInputStream(resources.file))).asObject()
        ScriptManager.setClasspath(File(resources.file.parent, "scripts"))

        val jinfo = jroot.get("info")
        val jpreferences = jroot.get("preferences")
        val jlayouts = jroot.get("layouts")
        val jtextures = jroot.get("textures")
        val jfonts = jroot.get("fonts")
        val jsounds = jroot.get("sounds")
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
        if (jsounds is JsonArray) {
            loadSounds(jsounds)
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

        postLoad(jroot)

        // Now all costume have been loaded, lets add the costume events.
        costumeEvents.forEach { data ->
            data.costumeEvent.costumes.add(resources.costumes.find(data.costumeName)!!)
        }

        inheritedCostumeEvents.forEach { costumeName, inheritsFromCostumeName ->
            val from = resources.costumes.find(costumeName)
            val to = resources.costumes.find(inheritsFromCostumeName)
            if (from != null && to != null) {
                from.inheritEventsFrom = to
            }
        }

        return resources
    }

    open protected fun postLoad(jroot: JsonObject) {}

    // EDITOR PREFERENCES

    open fun loadPreferences(jpreferences: JsonObject) {
        with(resources.preferences) {

            jpreferences.get("packages")?.let {
                val newPackages = it.asArray()
                packages.clear()
                newPackages.forEach {
                    packages.add(it.asString())
                }
            }

            outputFormat = EditorPreferences.JsonFormat.valueOf(jpreferences.getString("outputFormat", outputFormat.name))
            treeThumnailSize = jpreferences.getInt("treeThumbnailSize", treeThumnailSize)
            costumePickerThumbnailSize = jpreferences.getInt("costumePickerThumbnailSize", costumePickerThumbnailSize)
            isMaximized = jpreferences.getBoolean("isMaximized", false)
            windowWidth = jpreferences.getDouble("windowWidth", windowWidth)
            windowHeight = jpreferences.getDouble("windowHeight", windowHeight)
            apiURL = jpreferences.getString("apiIRL", apiURL)
            // println("Loaded preferences : ${resources.preferences}")
        }
    }

    // INFO

    fun loadInfo(jinfo: JsonObject) {
        with(resources.gameInfo) {
            title = jinfo.getString("title", "Tickle Game")
            id = jinfo.getString("id", "ticklegame")
            width = jinfo.getInt("width", 800)
            height = jinfo.getInt("height", 600)
            resizable = jinfo.getBoolean("resizable", true)
            fullScreen = jinfo.getBoolean("fullScreen", false)

            initialScenePath = resources.scenePathToFile(jinfo.getString("initialScene", "menu"))
            testScenePath = resources.scenePathToFile(jinfo.getString("testScene", "menu"))
            producerString = jinfo.getString("producer", NoProducer::class.java.name)
        }

        val jphysics = jinfo.get("physics")?.asObject()
        if (jphysics != null) {
            resources.gameInfo.physicsEngine = true
            if (resources.gameInfo.physicsEngine) {
                with(resources.gameInfo.physicsInfo) {
                    gravity.x = jphysics.getDouble("gravity_x", 0.0)
                    gravity.y = jphysics.getDouble("gravity_y", 0.0)
                    framesPerSecond = jphysics.getInt("framesPerSecond", 30)
                    velocityIterations = jphysics.getInt("velocityIterations", 8)
                    positionIterations = jphysics.getInt("positionIterations", 3)
                    scale = jphysics.getDouble("scale", 100.0)
                    filterGroupsString = jphysics.getString("filterGroups", NoFilterGroups::class.java.name)
                    filterBitsString = jphysics.getString("filterBits", NoFilterBits::class.java.name)
                }
            }
        }
        // println("Loaded info : $title : $width x $height Resize? $resizable. Game=$producerString")
    }


    // LAYOUTS

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
                    layoutStage.isDefault = jstage.getBoolean("isDefault", false)
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

    fun loadPoses(jposes: JsonArray) {
        for (jele in jposes) {
            val jpose = jele.asObject()
            val name = jpose.get("name").asString()
            val textureName = jpose.get("texture").asString()
            val texture = resources.textures.find(textureName)
            if (texture == null) {
                error("Texture $textureName not found for pose $name.")
                continue
            }
            val pose = Pose(texture)

            pose.rect.left = jpose.get("left").asInt()
            pose.rect.bottom = jpose.get("bottom").asInt()
            pose.rect.right = jpose.get("right").asInt()
            pose.rect.top = jpose.get("top").asInt()

            pose.offsetX = jpose.get("offsetX").asDouble()
            pose.offsetY = jpose.get("offsetY").asDouble()

            pose.direction.degrees = jpose.get("direction").asDouble()
            pose.tiled = jpose.getBoolean("tiled", false)

            pose.updateRectd()

            jpose.get("snapPoints")?.let {
                val jsnaps = it.asArray()
                jsnaps.forEach {
                    val jsnap = it.asObject()
                    val x = jsnap.getDouble("x", 0.0)
                    val y = jsnap.getDouble("y", 0.0)
                    pose.snapPoints.add(Vector2d(x, y))
                }
            }

            resources.poses.add(name, pose)
            //println("Loaded pose $name : ${pose}")
        }
    }

    // Costume Groups

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

    fun saveBody(jcostume: JsonObject, bodyDef: TickleBodyDef) {
        val jbody = JsonObject()

        with(bodyDef) {
            jbody.add("bodyType", type.name)
            if (type != BodyType.STATIC) {
                jbody.add("linearDamping", linearDamping)
                jbody.add("angularDamping", angularDamping)
                jbody.add("bullet", bullet)
                jbody.add("fixedRotation", fixedRotation)
            }
        }

        val jfixtures = JsonArray()
        jbody.add("fixtures", jfixtures)
        bodyDef.fixtureDefs.forEach { fixtureDef ->
            val jfixture = JsonObject()

            with(fixtureDef) {
                jfixture.add("friction", friction)
                jfixture.add("density", density)
                jfixture.add("restitution", restitution)
                jfixture.add("isSensor", isSensor)
            }

            with(fixtureDef.filter) {
                jfixture.add("group", groupIndex)
                jfixture.add("category", categoryBits)
                jfixture.add("mask", maskBits)
            }

            with(fixtureDef.shapeDef) {
                when (this) {
                    is CircleDef -> {
                        val jcircle = JsonObject()
                        jfixture.add("circle", jcircle)
                        jcircle.add("x", center.x)
                        jcircle.add("y", center.y)
                        jcircle.add("radius", radius)
                    }

                    is BoxDef -> {
                        val jbox = JsonObject()
                        jfixture.add("box", jbox)
                        jbox.add("x", center.x)
                        jbox.add("y", center.y)
                        jbox.add("width", width)
                        jbox.add("height", height)
                        jbox.add("angle", angle.degrees)
                    }

                    is PolygonDef -> {
                        val jpolygon = JsonObject()
                        jfixture.add("polygon", jpolygon)
                        val jpoints = JsonArray()
                        jpolygon.add("points", jpoints)
                        points.forEach { point ->
                            val jpoint = JsonObject()
                            jpoint.add("x", point.x)
                            jpoint.add("y", point.y)
                            jpoints.add(jpoint)
                        }
                    }

                    else -> {
                        System.err.println("ERROR. Unknown shape ${this.javaClass}")
                    }
                }
            }
            jfixtures.add(jfixture)

        }

        jcostume.add("body", jbody)
    }

    data class CostumeEventData(val costumeEvent: CostumeEvent, val costumeName: String)

    fun loadCostumes(jcostumes: JsonArray, group: ResourceMap<Costume>) {

        jcostumes.forEach {
            val jcostume = it.asObject()
            val name = jcostume.get("name").asString()
            val costume = Costume()
            try {
                costume.roleString = jcostume.getString("role", "")
            } catch (e: Exception) {
                error(e, "Failed to create role : ${jcostume.getString("role", "")}")
            }
            costume.canRotate = jcostume.getBoolean("canRotate", false)
            costume.canScale = jcostume.getBoolean("canScale", false)
            costume.zOrder = jcostume.getDouble("zOrder", 0.0)
            costume.initialEventName = jcostume.getString("initialEvent", "default")
            costume.showInSceneEditor = jcostume.getBoolean("showInSceneEditor", true)

            val inheritsFrom: String? = jcostume.getString("inheritsEventsFrom", null)
            if (inheritsFrom != null) {
                inheritedCostumeEvents[name] = inheritsFrom
            }

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
                            val halign = TextHAlignment.valueOf(jtextStyle.getString("halign", TextHAlignment.LEFT.name))
                            val valign = TextVAlignment.valueOf(jtextStyle.getString("valign", TextVAlignment.BASELINE.name))
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

                    jevent.get("sounds")?.let {
                        val jsounds = it.asArray()
                        jsounds.forEach {
                            val soundName = it.asString()
                            resources.sounds.find(soundName)?.let { event.sounds.add(it) }
                        }
                    }

                    jevent.get("ninePatches")?.let {
                        val jninePatches = it.asArray()
                        jninePatches.forEach {
                            val jninePatch = it.asObject()
                            val pose = resources.poses.find(jninePatch.get("pose").asString())
                            if (pose != null) {
                                val ninePatch = NinePatch(
                                        pose,
                                        jninePatch.getInt("left", 0),
                                        jninePatch.getInt("bottom", 0),
                                        jninePatch.getInt("right", 0),
                                        jninePatch.getInt("top", 0)
                                )
                                event.ninePatches.add(ninePatch)
                            }

                        }
                    }

                    costume.events[eventName] = event
                }
            }

            JsonUtil.loadAttributes(jcostume, costume.attributes)

            val jbody = jcostume.get("body")
            if (jbody != null && jbody.isObject) {
                loadBody(jbody.asObject(), costume)
            }

            group.add(name, costume)
            if (group is CostumeGroup) {
                costume.costumeGroup = group
            }
            // println("Loaded costume $name : ${costume}")
        }

    }

    fun loadBody(jbody: JsonObject, costume: Costume) {
        val bodyDef = TickleBodyDef()
        with(bodyDef) {
            type = BodyType.valueOf(jbody.getString("bodyType", BodyType.DYNAMIC.name))
            linearDamping = jbody.getDouble("linearDamping", 0.0)
            angularDamping = jbody.getDouble("angularDamping", 0.0)
            bullet = jbody.getBoolean("bullet", false)
            fixedRotation = jbody.getBoolean("fixedRotation", false)
        }
        jbody.get("fixtures")?.let {
            val jfixtures = it.asArray()
            jfixtures.forEach {
                val jfixture = it.asObject()
                var shape: ShapeDef? = null
                jfixture.get("circle")?.let {
                    val jcircle = it.asObject()
                    val x = jcircle.getDouble("x", 0.0)
                    val y = jcircle.getDouble("y", 0.0)
                    val radius = jcircle.getDouble("radius", 0.0)
                    val circle = CircleDef(Vector2d(x, y), radius)
                    shape = circle
                }
                jfixture.get("box")?.let {
                    val jbox = it.asObject()
                    val x = jbox.getDouble("x", 0.0)
                    val y = jbox.getDouble("y", 0.0)
                    val width = jbox.getDouble("width", 0.0)
                    val height = jbox.getDouble("height", 0.0)
                    val angle = jbox.getDouble("angle", 0.0)
                    val box = BoxDef(width, height, Vector2d(x, y), Angle.degrees(angle))
                    shape = box
                }
                jfixture.get("polygon")?.let {
                    val jpolygon = it.asObject()
                    val jpoints = jpolygon.get("points").asArray()
                    val polygon = PolygonDef()
                    jpoints.forEach {
                        val jpoint = it.asObject()
                        val x = jpoint.getDouble("x", 0.0)
                        val y = jpoint.getDouble("y", 0.0)
                        polygon.points.add(Vector2d(x, y))
                    }
                    shape = polygon
                }

                if (shape != null) {
                    val fixtureDef = TickleFixtureDef(shape!!)
                    with(fixtureDef) {
                        density = jfixture.getFloat("density", 1f)
                        restitution = jfixture.getFloat("restitution", 0f)
                        friction = jfixture.getFloat("friction", 0f)
                        isSensor = jfixture.getBoolean("isSensor", false)
                    }
                    with(fixtureDef.filter) {
                        groupIndex = jfixture.getInt("group", 0)
                        categoryBits = jfixture.getInt("category", 0xFFFF)
                        maskBits = jfixture.getInt("mask", 0xFFFF)
                    }
                    bodyDef.fixtureDefs.add(fixtureDef)
                }
            }
        }
        costume.bodyDef = bodyDef
    }

    // INPUTS

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

    fun loadFonts(jfonts: JsonArray) {

        jfonts.forEach { jele ->
            val jfont = jele.asObject()
            val name = jfont.get("name").asString()
            val fontResource = FontResource()
            val pngFilename = jfont.getString("pngFile", File(resources.texturesDirectory, "$name.png").path)
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

            val pngFile = resources.fromPath(pngFilename)
            if (pngFile.exists()) {
                fontResource.pngFile = pngFile
                fontResource.loadFromFile(pngFile)
            }

            resources.fontResources.add(name, fontResource)
            // println("Loaded font $name : ${fontResource}")

        }
    }


    // SOUNDS

    fun loadSounds(jsounds: JsonArray) {
        jsounds.forEach {
            val jsound = it.asObject()
            val name = jsound.get("name").asString()
            val file = resources.fromPath(jsound.get("file").asString())
            resources.sounds.add(name, Sound(file))

            // println("Loaded texture $name : $file")
        }
    }

    fun error(message: String) {
        ErrorHandler.handleError(JsonLoadException(message))
    }

    fun error(e: Exception, message: String) {
        ErrorHandler.handleError(JsonLoadException(e, message))
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

class JsonLoadException : Exception {
    constructor(message: String) : super(message)
    constructor(e: Exception, message: String) : super(message, e)
}

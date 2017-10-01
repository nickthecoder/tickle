package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.demo.NoProducer
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.events.KeyEventType
import uk.co.nickthecoder.tickle.events.KeyInput
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
            jinfo.add("startScene", startScene)
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
            startScene = jinfo.getString("startScene", "splash")
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
            jlayout.add("name", name)

            val jstages = JsonArray()
            jlayout.add("stages", jstages)

            layout.stages.forEach { stageName, layoutStage ->
                val jstage = JsonObject()
                jstage.add("name", stageName)
                jstage.add("stage", layoutStage.stageString)
            }

            val jviews = JsonArray()
            jlayout.add("views", jviews)

            layout.views.forEach { viewName, layoutView ->
                val jview = JsonObject()
                jview.add("name", viewName)
                jview.add("view", layoutView.viewString)
                if (layoutView.stageName.isNotBlank()) {
                    jview.add("stage", layoutView.stageName)
                }

                with(layoutView.position) {
                    jview.add(if (leftAligned) "left" else "right", leftRightMargin)
                    width?.let { jview.add("width", it) }
                    widthRatio?.let { jview.add("widthRatio", it) }

                    jview.add(if (bottomAligned) "bottom" else "top", topBottomMargin)
                    height?.let { jview.add("height", it) }
                    heightRatio?.let { jview.add("heightRatio", it) }
                }
            }
        }
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
                    layout.stages[stageName] = layoutStage
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


                    val left = jview.getInt("left", -1)
                    if (left >= 0) {
                        layoutView.position.leftRightMargin = left
                        layoutView.position.leftAligned = true
                    } else {
                        var right = jview.getInt("right", -1)
                        if (right < 0) {
                            System.err.println("ERROR. Neither left nor right specified for view $viewName in layout $name")
                            right = 0
                        }
                        layoutView.position.leftRightMargin = right
                        layoutView.position.leftAligned = false
                    }

                    val bottom = jview.getInt("bottom", -1)
                    if (bottom >= 0) {
                        layoutView.position.topBottomMargin = bottom
                        layoutView.position.bottomAligned = true
                    } else {
                        var top = jview.getInt("top", -1)
                        if (top < 0) {
                            System.err.println("ERROR. Neither top nor bottom specified for view $viewName in layout $name")
                            top = 0
                        }
                        layoutView.position.topBottomMargin = top
                        layoutView.position.bottomAligned = false
                    }

                    val width = jview.getInt("width", -1)
                    if (width > 0) {
                        layoutView.position.width = width
                    } else {
                        val widthRatio = jview.getFloat("widthRatio", -1f)
                        layoutView.position.widthRatio = if (widthRatio > 0) widthRatio else null
                    }

                    val height = jview.getInt("height", -1)
                    if (height > 0) {
                        layoutView.position.height = height
                    } else {
                        val heightRatio = jview.getFloat("heightRatio", -1f)
                        layoutView.position.heightRatio = if (heightRatio > 0) heightRatio else null
                    }
                    layout.views[viewName] = layoutView
                }
            }
            resources.addLayout(name, layout)

        }
    }

    // TEXTURES

    fun saveTextures(): JsonArray {
        val jtextures = JsonArray()
        resources.textures().forEach { name, textureResource ->
            val jtexture = JsonObject()
            jtexture.add("name", name)
            jtexture.add("file", toPath(textureResource.file))
            jtextures.add(jtexture)
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
                jpose.add("direction", pose.directionDegrees)

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

            pose.offsetX = jpose.get("offsetX").asFloat()
            pose.offsetY = jpose.get("offsetY").asFloat()

            pose.directionDegrees = jpose.get("direction").asDouble()
            pose.updateRectf()

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
            val jevents = JsonArray()
            jcostume.add("events", jevents)
            costume.events.forEach { eventName, event ->
                val jevent = JsonObject()
                jevent.add("name", eventName)
                event.pose?.let { pose ->
                    resources.findPoseName(pose)?.let { poseName ->
                        jevent.add("pose", poseName)
                    }
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

            jcostume.get("events")?.let {
                val jevents = it.asArray()
                jevents.forEach {
                    val jevent = it.asObject()
                    val eventName = jevent.get("name").asString()
                    val event = CostumeEvent()
                    jevent?.get("pose")?.let {
                        val pose = resources.optionalPose(it.asString())
                        event.pose = pose
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
            jinput.add("keys", jkeys)

            jinputs.add(jinput)
        }
        return jinputs
    }

    fun addKeyInputs(input: Input, toArray: JsonArray) {
        if (input is KeyInput) {
            val jkey = JsonObject()
            jkey.add("key", input.key)
            jkey.add("type", input.type.name)
            toArray.add(jkey)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addKeyInputs(it, toArray)
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
                    val key = jkey.get("key").asInt()
                    val typeString = jkey.get("type").asString()
                    val type = KeyEventType.valueOf(typeString)
                    input.add(KeyInput(key, type))
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

package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.tickle.Layout
import uk.co.nickthecoder.tickle.LayoutView
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
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

    fun load(file: File) {
        resources.file = file
        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

        val jinfo = jroot.get("info")
        val jlayouts = jroot.get("layouts")
        val jtextures = jroot.get("textures")
        val jposes = jroot.get("poses")
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
        if (jinputs is JsonArray) {
            loadInputs(jinputs)
        }

    }

    fun loadInfo(jinfo: JsonObject) {
        with(resources.gameInfo) {
            title = jinfo.getString("title", "Tickle Game")
            width = jinfo.getInt("width", 800)
            height = jinfo.getInt("height", 600)
            resizable = jinfo.getBoolean("resizable", true)
            producerString = jinfo.getString("producer", NoProducer::javaClass.name)

            println("Loaded info : $title : $width x $height Resize? $resizable. Game=$producerString")
        }
    }

    fun loadLayouts(jlayouts: JsonArray) {
        jlayouts.forEach { jele ->
            val jlayout = jele.asObject()
            val name = jlayout.get("name").asString()
            val layout = Layout()
            jlayout.get("views")?.let {
                val jviews = it.asArray()
                jviews.forEach {
                    val jview = it.asObject()
                    val layoutView = LayoutView()
                    val viewName = jview.get("name").asString()
                    layoutView.viewString = jview.get("view").asString()

                    val left = jview.getInt("left", -1)
                    if (left >= 0) {
                        layoutView.leftRightMargin = left
                        layoutView.leftAligned = true
                    } else {
                        var right = jview.getInt("right", -1)
                        if (right < 0) {
                            System.err.println("ERROR. Neither left nor right specified for view $viewName in layout $name")
                            right = 0
                        }
                        layoutView.leftRightMargin = right
                        layoutView.leftAligned = false
                    }

                    val bottom = jview.getInt("bottom", -1)
                    if (bottom >= 0) {
                        layoutView.topBottomMargin = bottom
                        layoutView.bottomAligned = true
                    } else {
                        var top = jview.getInt("top", -1)
                        if (top < 0) {
                            System.err.println("ERROR. Neither top nor bottom specified for view $viewName in layout $name")
                            top = 0
                        }
                        layoutView.topBottomMargin = top
                        layoutView.bottomAligned = false
                    }

                    val width = jview.getInt("width", -1)
                    if (width > 0) {
                        layoutView.width = width
                    } else {
                        val widthRatio = jview.getFloat("widthRatio", -1f)
                        layoutView.widthRatio = if (widthRatio > 0) widthRatio else null
                    }

                    val height = jview.getInt("height", -1)
                    if (height > 0) {
                        layoutView.height = height
                    } else {
                        val heightRatio = jview.getFloat("heightRatio", -1f)
                        layoutView.heightRatio = if (heightRatio > 0) heightRatio else null
                    }
                    layout.views[viewName] = layoutView
                }
            }
            resources.addLayout(name, layout)

        }
    }

    fun loadTextures(jtextures: JsonArray) {
        jtextures.forEach { jele ->
            val jtexture = jele.asObject()
            val name = jtexture.get("name").asString()
            val file = fromPath(jtexture.get("file").asString())
            resources.addTexture(name, file)

            println("Loaded texture $name : $file")
        }
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
            println("Loaded pose $name : ${pose}")
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
            println("Loaded input $name : $input")
        }
    }

    fun save(file: File) {

        resources.file = file.absoluteFile

        val jroot = JsonObject()
        jroot.add("info", createJsonInfo())
        jroot.add("layouts", createJsonLayouts())
        jroot.add("textures", createJsonTextures())
        jroot.add("poses", createJsonPoses())
        jroot.add("inputs", createJsonInputs())

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }
    }

    fun toPath(file: File): String {
        try {
            return file.absoluteFile.toRelativeString(resources.resourceDirectory)
        } catch(e: Exception) {
            return file.absolutePath
        }
    }

    fun fromPath(path: String): File {
        return resources.resourceDirectory.resolve(path)
    }


    fun createJsonInfo(): JsonObject {
        val jinfo = JsonObject()
        jinfo.add("title", resources.gameInfo.title)
        jinfo.add("width", resources.gameInfo.width)
        jinfo.add("height", resources.gameInfo.height)
        jinfo.add("resizable", resources.gameInfo.resizable)
        jinfo.add("producer", resources.gameInfo.producerString)
        return jinfo
    }


    fun createJsonLayouts(): JsonArray {
        val jlayouts = JsonArray()
        resources.layouts().forEach { name, layout ->
            val jlayout = JsonObject()
            jlayout.add("name", name)
            val jviews = JsonArray()
            jlayout.add("views", jviews)

            layout.views.forEach { viewName, layoutView ->
                val jview = JsonObject()
                jview.add("name", viewName)
                jview.add("view", layoutView.viewString)

                jview.add(if (layoutView.leftAligned) "left" else "right", layoutView.leftRightMargin)
                layoutView.width?.let { jview.add("width", it) }
                layoutView.widthRatio?.let { jview.add("widthRatio", it) }

                jview.add(if (layoutView.bottomAligned) "bottom" else "top", layoutView.topBottomMargin)
                layoutView.height?.let { jview.add("height", it) }
                layoutView.heightRatio?.let { jview.add("heightRatio", it) }
            }
        }
        return jlayouts
    }

    fun createJsonTextures(): JsonArray {
        val jtextures = JsonArray()
        resources.textures().forEach { name, textureResource ->
            val jtexture = JsonObject()
            jtexture.add("name", name)
            jtexture.add("file", toPath(textureResource.file))
            jtextures.add(jtexture)
        }
        return jtextures
    }

    fun createJsonPoses(): JsonArray {
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

    fun createJsonInputs(): JsonArray {
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

}


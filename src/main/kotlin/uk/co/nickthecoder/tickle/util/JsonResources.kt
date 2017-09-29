package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.tickle.DefaultGame
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
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
        val jtextures = jroot.get("textures")
        val jposes = jroot.get("poses")
        val jinputs = jroot.get("inputs")

        if (jinfo is JsonObject) {
            loadInfo(jinfo)
        }
        if (jtextures is JsonArray) {
            loadTextures(jtextures)
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
            gameClassString = jinfo.getString("gameClass", DefaultGame::javaClass.name)

            println("Loaded info : $title : $width x $height Resize? $resizable. Game=$gameClassString")
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
        jinfo.add("gameClass", resources.gameInfo.gameClassString)
        return jinfo
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


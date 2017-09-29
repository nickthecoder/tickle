package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.events.KeyInput
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

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
                jpose.add("top", pose.rect.bottom)
                jpose.add("width", pose.rect.width)
                jpose.add("height", pose.rect.topDownHeight)
                jpose.add("offsetX", pose.offsetX)
                jpose.add("direction", pose.directionDegrees)
                jpose.add("offsetY", pose.offsetY)

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


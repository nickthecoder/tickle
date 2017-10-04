package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.SceneStage
import uk.co.nickthecoder.tickle.demo.NoDirector
import uk.co.nickthecoder.tickle.graphics.Color
import java.io.*

class JsonScene {

    val sceneResource: SceneResource

    constructor(file: File) {
        sceneResource = SceneResource()
        load(file)
    }

    constructor(sceneResource: SceneResource) {
        this.sceneResource = sceneResource
    }

    fun save(file: File) {
        sceneResource.file = file.absoluteFile

        val jroot = JsonObject()
        jroot.add("director", sceneResource.directorString)
        jroot.add("background", sceneResource.background.toHashRGB())
        jroot.add("layout", sceneResource.layoutName)
        val jstages = JsonArray()
        jroot.add("stages", jstages)
        sceneResource.sceneStages.forEach { stageName, sceneStage ->
            jstages.add(saveStage(stageName, sceneStage))
        }

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }
    }

    fun load(file: File) {
        sceneResource.file = file
        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

        sceneResource.directorString = jroot.getString("director", NoDirector::class.java.name)
        sceneResource.layoutName = jroot.getString("layout", "default")
        sceneResource.background = Color.fromString(jroot.getString("background", "#FFFFFF"))

        jroot.get("stages")?.let {
            val jstages = it.asArray()
            jstages.forEach {
                loadStage(it.asObject())
            }
        }
    }


    fun saveStage(stageName: String, sceneStage: SceneStage): JsonObject {
        val jstage = JsonObject()
        jstage.add("name", stageName)
        val jactors = JsonArray()
        jstage.add("actors", jactors)

        sceneStage.sceneActors.forEach { sceneActor ->
            val jactor = JsonObject()
            jactors.add(jactor)
            jactor.add("costume", sceneActor.costumeName)
            jactor.add("x", sceneActor.x)
            jactor.add("y", sceneActor.y)
            jactor.add("direction", sceneActor.direction)

            if (sceneActor.attributes.map.isNotEmpty()) {
                val jattributes = JsonArray()
                jactor.add("attributes", jattributes)
                sceneActor.attributes.map.forEach { attributeName, attributeValue ->
                    val jattribute = JsonObject()
                    jattributes.add(jattribute)
                    jattribute.add("name", attributeName)
                    jattribute.add("value", attributeValue)
                }
            }
        }
        return jstage
    }

    fun loadStage(jstage: JsonObject) {
        val name = jstage.getString("name", "default")
        val sceneStage = SceneStage()
        sceneResource.sceneStages[name] = sceneStage

        jstage.get("actors")?.let {
            val jactors = it.asArray()
            jactors.forEach {
                loadActor(sceneStage, it.asObject())
            }
        }
    }

    fun loadActor(sceneStage: SceneStage, jactor: JsonObject) {
        val sceneActor = SceneActor()
        sceneActor.costumeName = jactor.get("costume").asString()
        sceneActor.x = jactor.getFloat("x", 0f)
        sceneActor.y = jactor.getFloat("y", 0f)
        sceneActor.direction = jactor.getDouble("direction", 0.0)

        JsonUtil.loadAttributes(jactor, sceneActor.attributes)

        sceneStage.sceneActors.add(sceneActor)
    }

}

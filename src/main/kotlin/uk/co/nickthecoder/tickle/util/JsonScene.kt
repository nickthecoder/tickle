package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.SceneStage
import uk.co.nickthecoder.tickle.demo.NoDirector
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class JsonScene {

    val sceneResource: SceneResource

    constructor(file: File) {
        sceneResource = SceneResource()
        load(file)
    }

    constructor(sceneResource: SceneResource) {
        this.sceneResource = sceneResource
    }

    fun load(file: File) {
        sceneResource.file = file
        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

        sceneResource.directorString = jroot.getString("director", NoDirector::class.java.name)
        sceneResource.layoutName = jroot.getString("layout", "default")

        jroot.get("stages")?.let {
            val jstages = it.asArray()
            jstages.forEach {
                loadStage(it.asObject())
            }
        }
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

        JsonUtil.loadAttributes( jactor, sceneActor.attributes)

        sceneStage.sceneActors.add(sceneActor)
    }

}

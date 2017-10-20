package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.tickle.NoDirector
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.resources.*
import java.io.*

class JsonScene {

    val sceneResource: SceneResource

    /**
     * When creating ActorResource, when designing, an instance of Role will be created, so that default values
     * for its attributes can be established. When loading a scene during real play, there is no need for this,
     * and therefore it is skipped.
     */
    private val isDesigning: Boolean

    constructor(file: File, isDesigning: Boolean = false) {
        sceneResource = SceneResource()
        this.isDesigning = isDesigning
        load(file)
    }

    constructor(sceneResource: SceneResource) {
        isDesigning = false
        this.sceneResource = sceneResource
    }

    fun save(file: File) {
        sceneResource.file = Resources.instance.sceneDirectory.resolve(file)

        val jroot = JsonObject()
        jroot.add("director", sceneResource.directorString)
        JsonUtil.saveAttributes(jroot, sceneResource.directorAttributes, "directorAttributes")

        jroot.add("background", sceneResource.background.toHashRGB())
        jroot.add("showMouse", sceneResource.showMouse)

        jroot.add("layout", sceneResource.layoutName)
        val jstages = JsonArray()
        jroot.add("stages", jstages)
        sceneResource.stageResources.forEach { stageName, stageResource ->
            jstages.add(saveStage(stageName, stageResource))
        }

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, Resources.instance.gameInfo.outputFormat.writerConfig)
        }
    }

    fun load(file: File) {
        sceneResource.file = Resources.instance.sceneDirectory.resolve(file)

        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

        sceneResource.directorString = jroot.getString("director", NoDirector::class.java.name)
        JsonUtil.loadAttributes(jroot, sceneResource.directorAttributes, "directorAttributes")

        sceneResource.layoutName = jroot.getString("layout", "default")
        sceneResource.background = Color.fromString(jroot.getString("background", "#FFFFFF"))
        sceneResource.showMouse = jroot.getBoolean("showMouse", true)

        sceneResource.layoutName
        jroot.get("stages")?.let {
            val jstages = it.asArray()
            jstages.forEach {
                loadStage(it.asObject())
            }
        }
    }


    fun saveStage(stageName: String, stageResource: StageResource): JsonObject {
        val jstage = JsonObject()
        jstage.add("name", stageName)
        val jactors = JsonArray()
        jstage.add("actors", jactors)

        stageResource.actorResources.forEach { actorResource ->
            val jactor = JsonObject()
            jactors.add(jactor)
            jactor.add("costume", actorResource.costumeName)
            jactor.add("x", actorResource.x)
            jactor.add("y", actorResource.y)
            jactor.add("xAlignment", actorResource.xAlignment.name)
            jactor.add("yAlignment", actorResource.yAlignment.name)
            jactor.add("direction", actorResource.direction.degrees)
            jactor.add("scale", actorResource.scale)
            if (actorResource.pose == null) {
                jactor.add("text", actorResource.text)
            }

            JsonUtil.saveAttributes(jactor, actorResource.attributes)
        }
        return jstage
    }

    fun loadStage(jstage: JsonObject) {
        val name = jstage.getString("name", "default")
        val stageResource = StageResource()
        sceneResource.stageResources[name] = stageResource

        jstage.get("actors")?.let {
            val jactors = it.asArray()
            jactors.forEach {
                loadActor(stageResource, it.asObject())
            }
        }
    }

    fun loadActor(stageResource: StageResource, jactor: JsonObject) {
        val actorResource = ActorResource(isDesigning)
        // NOTE. load the attributes FIRST, then change the costume, as that gives Attributes the chance to remove
        // attributes unsupported by the Role class.
        JsonUtil.loadAttributes(jactor, actorResource.attributes)
        actorResource.costumeName = jactor.get("costume").asString()

        actorResource.x = jactor.getDouble("x", 0.0)
        actorResource.y = jactor.getDouble("y", 0.0)
        actorResource.xAlignment = ActorXAlignment.valueOf(jactor.getString("xAlignment", "LEFT"))
        actorResource.yAlignment = ActorYAlignment.valueOf(jactor.getString("yAlignment", "BOTTOM"))

        actorResource.direction.degrees = jactor.getDouble("direction", 0.0)
        actorResource.scale = jactor.getDouble("scale", 1.0)
        actorResource.text = jactor.getString("text", "")

        stageResource.actorResources.add(actorResource)
    }

}

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

        val jgrid = JsonObject()
        jroot.add("grid", jgrid)
        with(sceneResource.grid) {
            jgrid.add("enabled", enabled == true)

            jgrid.add("xSpacing", spacing.x)
            jgrid.add("ySpacing", spacing.y)

            jgrid.add("xOffset", offset.x)
            jgrid.add("yOffset", offset.y)

            jgrid.add("xCloseness", closeness.x)
            jgrid.add("yCloseness", closeness.y)
        }

        val jguides = JsonObject()
        jroot.add("guides", jguides)
        with(sceneResource.guides) {
            jguides.add("enabled", enabled)
            jguides.add("closeness", closeness)

            val jx = JsonArray()
            xGuides.forEach { jx.add(it) }
            jguides.add("x", jx)

            val jy = JsonArray()
            yGuides.forEach { jy.add(it) }
            jguides.add("y", jy)
        }

        val jincludes = JsonArray()
        jroot.add("include", jincludes)
        sceneResource.includes.forEach { include ->
            jincludes.add(Resources.instance.sceneFileToPath(include))
        }

        val jstages = JsonArray()
        jroot.add("stages", jstages)
        sceneResource.stageResources.forEach { stageName, stageResource ->
            jstages.add(saveStage(stageName, stageResource))
        }

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, Resources.instance.preferences.outputFormat.writerConfig)
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

        jroot.get("grid")?.let {
            val jgrid = it.asObject()
            with(sceneResource.grid) {
                enabled = jgrid.getBoolean("enabled", false)

                spacing.x = jgrid.getDouble("xSpacing", 50.0)
                spacing.y = jgrid.getDouble("ySpacing", 50.0)

                offset.x = jgrid.getDouble("xOffset", 0.0)
                offset.y = jgrid.getDouble("yOffset", 0.0)

                closeness.x = jgrid.getDouble("xCloseness", 10.0)
                closeness.y = jgrid.getDouble("yCloseness", 10.0)
            }
            //println("Loaded grid ${sceneResource.grid}")
        }

        jroot.get("guides")?.let {
            val jguides = it.asObject()
            with(sceneResource.guides) {
                enabled = jguides.getBoolean("enabled", true)
                closeness = jguides.getDouble("closeness", 10.0)

                jguides.get("x")?.let {
                    val jx = it.asArray()
                    jx.forEach { xGuides.add(it.asDouble()) }
                }
                jguides.get("y")?.let {
                    val jy = it.asArray()
                    jy.forEach { yGuides.add(it.asDouble()) }
                }
            }
            //println("Loaded guides ${sceneResource.guides}")
        }

        jroot.get("include")?.let {
            val jincludes = it.asArray()
            jincludes.forEach { jinclude ->
                sceneResource.includes.add(Resources.instance.scenePathToFile(jinclude.asString()))
            }
        }

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

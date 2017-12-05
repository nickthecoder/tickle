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
        jroot.add("snapToGrid", jgrid)
        with(sceneResource.snapToGrid) {
            jgrid.add("enabled", enabled == true)

            jgrid.add("xSpacing", spacing.x)
            jgrid.add("ySpacing", spacing.y)

            jgrid.add("xOffset", offset.x)
            jgrid.add("yOffset", offset.y)

            jgrid.add("xCloseness", closeness.x)
            jgrid.add("yCloseness", closeness.y)
        }

        val jguides = JsonObject()
        jroot.add("snapToGuides", jguides)
        with(sceneResource.snapToGuides) {
            jguides.add("enabled", enabled)
            jguides.add("closeness", closeness)

            val jx = JsonArray()
            xGuides.forEach { jx.add(it) }
            jguides.add("x", jx)

            val jy = JsonArray()
            yGuides.forEach { jy.add(it) }
            jguides.add("y", jy)
        }

        val jothers = JsonObject()
        jroot.add("snapToOthers", jothers)
        with(sceneResource.snapToOthers) {
            jothers.add("enabled", enabled)

            jothers.add("xCloseness", closeness.x)
            jothers.add("yCloseness", closeness.y)
        }

        val jrotation = JsonObject()
        jroot.add("snapRotation", jrotation)
        with(sceneResource.snapRotation) {
            jrotation.add("enabled", enabled)
            jrotation.add("step", stepDegrees)
            jrotation.add("closeness", closeness)
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

        jroot.get("snapToGrid")?.let {
            val jgrid = it.asObject()
            with(sceneResource.snapToGrid) {
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

        jroot.get("snapToGuides")?.let {
            val jguides = it.asObject()
            with(sceneResource.snapToGuides) {
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

        jroot.get("snapToOthers")?.let {
            val jothers = it.asObject()
            with(sceneResource.snapToOthers) {
                enabled = jothers.getBoolean("enabled", true)
                closeness.x = jothers.getDouble("xCloseness", 10.0)
                closeness.y = jothers.getDouble("yCloseness", 10.0)
            }

        }

        jroot.get("snapRotation")?.let {
            val jrotation = it.asObject()
            with(sceneResource.snapRotation) {
                enabled = jrotation.getBoolean("enabled", true)
                stepDegrees = jrotation.getDouble("step", 15.0)
                closeness = jrotation.getDouble("closeness", 15.0)
            }

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
            val costume = Resources.instance.costumes.find(actorResource.costumeName)

            val jactor = JsonObject()
            jactors.add(jactor)
            with(actorResource) {
                jactor.add("costume", costumeName)
                jactor.add("x", x)
                jactor.add("y", y)
                if (zOrder != costume?.zOrder) { // Only save zOrders which are NOT the default zOrder for the Costume.
                    jactor.add("zOrder", zOrder)
                }

                if (isText()) {
                    jactor.add("textAlignmentX", xAlignment.name)
                    jactor.add("textAlignmentY", yAlignment.name)
                    jactor.add("text", text)
                }

                jactor.add("direction", direction.degrees)

                if (isSizable()) {
                    jactor.add("sizeX", size.x)
                    jactor.add("sizeY", size.y)
                    jactor.add("alignmentX", alignment.x)
                    jactor.add("alignmentY", alignment.y)
                } else {
                    jactor.add("scaleX", scale.x)
                    jactor.add("scaleY", scale.y)
                }

                if (flipX) {
                    jactor.add("flipX", true)
                }
                if (flipY) {
                    jactor.add("flipY", true)
                }

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
        val costume = Resources.instance.costumes.find(actorResource.costumeName)

        with(actorResource) {
            x = jactor.getDouble("x", 0.0)
            y = jactor.getDouble("y", 0.0)
            zOrder = jactor.getDouble("zOrder", costume?.zOrder ?: 0.0)

            // xAlignment was the old name, textAlignmentX is the new name.
            xAlignment = ActorXAlignment.valueOf(jactor.getString("textAlignmentX", jactor.getString("xAlignment", "LEFT")))
            yAlignment = ActorYAlignment.valueOf(jactor.getString("textAlignmentY", jactor.getString("yAlignment", "BOTTOM")))

            direction.degrees = jactor.getDouble("direction", 0.0)

            if (isSizable()) {
                val rect = costume?.chooseNinePatch(costume.initialEventName)?.pose?.rect
                size.x = jactor.getDouble("sizeX", rect?.width?.toDouble() ?: 1.0)
                size.y = jactor.getDouble("sizeY", rect?.height?.toDouble() ?: 1.0)
                alignment.x = jactor.getDouble("alignmentX", 0.5)
                alignment.y = jactor.getDouble("alignmentY", 0.5)
            } else {
                // Legacy (when scale was a single Double, rather than a Vector2d)
                jactor.get("scale")?.let {
                    scale.x = it.asDouble()
                    scale.y = it.asDouble()
                }
                // This is the new form of scale.
                scale.x = jactor.getDouble("scaleX", 1.0)
                scale.y = jactor.getDouble("scaleY", 1.0)
            }
            flipX = jactor.getBoolean("flipX", false)
            flipY = jactor.getBoolean("flipY", false)
            actorResource.text = jactor.getString("text", "")
        }

        stageResource.actorResources.add(actorResource)
    }

}

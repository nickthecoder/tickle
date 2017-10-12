package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.util.Angle
import java.io.File

/**
 * Used when loading and editing a Scene. Not used during actual game play.
 */
class SceneResource {

    var file: File? = null

    var directorString: String = NoDirector::class.java.name

    var layoutName: String = ""
        set(v) {
            if (field != v) {
                field = v
                updateLayout()
            }
        }

    var background: Color = Color.BLACK

    /**
     * Keyed on the name of the stage
     */
    val sceneStages = mutableMapOf<String, SceneStage>()

    val listeners = mutableSetOf<SceneListerner>()


    /**
     * Gets the Layout to create the scene, and then populates the Stages with Actors.
     */
    fun createScene(): Scene {
        val layout = Resources.instance.layout(layoutName)
        val scene = layout.createScene()

        scene.background = background

        sceneStages.forEach { name, sceneStage ->
            val stage = scene.stages[name]
            if (stage == null) {
                System.err.println("ERROR. Stage $name not found. Ignoring all actors on that stage")
            } else {
                sceneStage.sceneActors.forEach { sceneActor ->
                    sceneActor.createActor()?.let { actor ->
                        stage.add(actor, false)
                    }
                }
            }
        }

        return scene
    }

    /**
     * Called when the layout has changed. Attempt to move all of the actors from like-names stages, but any
     * unmatches stage names will result in actors being put in a "random" stage.
     */
    private fun updateLayout() {

        val oldStages = sceneStages.toMap()
        sceneStages.clear()

        val layout = Resources.instance.layout(layoutName)
        layout.layoutStages.keys.forEach { stageName ->
            sceneStages[stageName] = SceneStage()
        }

        oldStages.forEach { stageName, oldStage ->
            if (sceneStages.containsKey(stageName)) {
                sceneStages[stageName]!!.sceneActors.addAll(oldStage.sceneActors)
            } else {
                if (oldStage.sceneActors.isNotEmpty()) {
                    System.err.println("Warning. Layout ${layoutName} doesn't have a stage called '${stageName}'. Placing actors in another stage.")
                    sceneStages.values.firstOrNull()?.let { firstStage ->
                        firstStage.sceneActors.addAll(oldStage.sceneActors)
                    }
                }
            }
        }
    }

    fun fireChange() {
        listeners.forEach {
            it.sceneChanged(this)
        }
    }
}

interface SceneListerner {

    fun sceneChanged(sceneResource: SceneResource)

}

/**
 * Details of all the Actors' initial state
 */
class SceneStage {

    val sceneActors = mutableListOf<SceneActor>()

}

class SceneActor(val isDesigning: Boolean = false) {

    var costumeName: String = ""
        set(v) {
            field = v
            updateAttributesMetaData()
        }

    var x: Double = 0.0
    var y: Double = 0.0
        set(v) {
            field = v
            if (v == 100.0) {
                Thread.dumpStack()
            }

        }

    val direction = Angle()

    val attributes = Attributes()

    val pose: Pose? by lazy { Resources.instance.optionalCostume(costumeName)?.events?.get("default")?.choosePose() }

    val textStyle: TextStyle? by lazy { Resources.instance.optionalCostume(costumeName)?.events?.get("default")?.chooseTextStyle() }

    var text: String = ""

    val displayText
        get() = if (text.isBlank()) "<no text>" else text

    fun createActor(): Actor? {
        val costume = Resources.instance.optionalCostume(costumeName)
        if (costume == null) {
            System.err.println("ERROR. Costume $costumeName not found in resources.")
            return null
        }
        val actor = costume.createActor(text)

        actor.x = x
        actor.y = y
        actor.direction.degrees = direction.degrees

        actor.role?.let { attributes.applyToObject(it) }

        return actor
    }

    private fun updateAttributesMetaData() {
        val roleString = Resources.instance.optionalCostume(costumeName)?.roleString
        if (roleString != null && roleString.isNotBlank()) {
            attributes.updateAttributesMetaData(roleString, isDesigning)
        }
    }

    override fun toString() = "SceneActor $costumeName @ $x , $y direction=$direction.degrees"
}

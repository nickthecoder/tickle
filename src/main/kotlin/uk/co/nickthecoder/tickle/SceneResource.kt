package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.demo.NoDirector
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Heading
import java.io.File

/**
 * Used when loading and editing a Scene. Not used during actual game play.
 */
class SceneResource {

    var file: File? = null

    var directorString: String = NoDirector::class.java.name

    var layoutName: String = "default"

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

class SceneActor {

    var costumeName: String = ""
        set(v) {
            field = v
            updateAttributeMetaData()
        }

    var x: Float = 0f
    var y: Float = 0f
    val direction = Heading()

    val attributes = Attributes()

    val pose: Pose? by lazy { Resources.instance.optionalCostume(costumeName)?.events?.get("default")?.choosePose() }

    fun createActor(): Actor? {
        val costume = Resources.instance.optionalCostume(costumeName)
        if (costume == null) {
            System.err.println("ERROR. Costume $costumeName not found in resources.")
            return null
        }
        val actor = costume.createActor()
        actor.x = x
        actor.y = y
        actor.direction.degrees = direction.degrees

        actor.role?.let { attributes.applyToObject(it) }

        return actor
    }

    private fun updateAttributeMetaData() {
        Resources.instance.optionalCostume(costumeName)?.roleString?.let { roleString ->
            attributes.updateAttributeMetaData(roleString)
        }
    }


    override fun toString() = "SceneActor $costumeName @ $x , $y direction=$direction.degrees"
}

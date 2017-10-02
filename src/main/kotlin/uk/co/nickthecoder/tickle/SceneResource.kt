package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.demo.NoDirector
import uk.co.nickthecoder.tickle.graphics.Color
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
}

/**
 * Details of all the Actors' initial state
 */
class SceneStage {

    val sceneActors = mutableListOf<SceneActor>()

}

class SceneActor {
    var costumeName: String = ""
    var x: Float = 0f
    var y: Float = 0f
    var direction: Double = 0.0
    val attributes = Attributes()

    fun createActor(): Actor? {
        val costume = Resources.instance.optionalCostume(costumeName)
        if (costume == null) {
            System.err.println("ERROR. Costume $costumeName not found in resources.")
            return null
        }
        val actor = costume.createActor()
        actor.x = x
        actor.y = y
        actor.directionDegrees = direction

        actor.role?.let { attributes.updateRole(it) }

        return actor
    }
}

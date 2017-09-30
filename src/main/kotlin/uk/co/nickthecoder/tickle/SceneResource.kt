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
    val attributes = mutableMapOf<String, String>()

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

        updateAttributes(actor.role)

        return actor
    }

    fun updateAttributes(role: Role?) {
        if (role == null) return
        val klass = role.javaClass

        attributes.forEach { name, value ->
            updateAttribute(role, klass, name, value)
        }
    }

    fun updateAttribute(role: Role, klass: Class<Role>, name: String, value: String) {
        try {
            try {
                val field = klass.getField(name)
                field.set(role, fromString(value, field.type))
            } catch (e: NoSuchFieldException) {
                val setterName = "set" + name.capitalize()
                val setter = klass.methods.filter { it.name == setterName && it.parameterCount == 1 }.firstOrNull()
                if (setter == null) {
                    System.err.println("ERROR. Could not find attribute $name in $role")
                } else {
                    val type = setter.parameterTypes[0]
                    setter.invoke(role, fromString(value, type))
                }
            }
        } catch (e: Exception) {
            System.err.println("Failed to set attribute $name on $role : $e")
        }
    }

    fun fromString(value: String, type: Class<*>): Any {
        return when (type) {
            Float::class.java -> value.toFloat()
            Int::class.java -> value.toInt()
            String::class.java -> value
            else -> throw IllegalArgumentException("Cannot convert from type $type")
        }
    }
}

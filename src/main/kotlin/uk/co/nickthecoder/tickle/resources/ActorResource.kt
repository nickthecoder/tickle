package uk.co.nickthecoder.tickle.resources

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.scene.StageLayer
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.util.Angle

enum class ActorXAlignment { LEFT, CENTER, RIGHT, RATIO }
enum class ActorYAlignment { BOTTOM, CENTER, TOP, RATIO }

/**
 * Details of an Actor's initial state.
 * Used when loading and editing a Scene. Not used during actual game play.
 */

class ActorResource(val isDesigning: Boolean = false) {

    var costumeName: String = ""
        set(v) {
            field = v
            updateAttributesMetaData()
            Resources.instance.costumes.find(costumeName)?.let { costume ->
                zOrder = costume.zOrder
            }
        }

    var x: Double = 0.0
    var y: Double = 0.0
    var zOrder: Double = 0.0

    var flipX = false
    var flipY = false

    var xAlignment: ActorXAlignment = ActorXAlignment.LEFT
    var yAlignment: ActorYAlignment = ActorYAlignment.BOTTOM

    /**
     * Used by SceneEditor in conjunction with StageConstraint. This is where the actor was dragged to, but [x],[y]
     * are the final position of the actor determined by the StageConstraint. When using NoStageConstraint,
     * draggedX,draggedY will be the same as x,y.
     */
    var draggedX: Double = 0.0

    var draggedY: Double = 0.0

    val direction = Angle()

    var scale = Vector2d(1.0, 1.0)

    /**
     * For NinePatchAppearance only
     */
    var size = Vector2d(1.0, 1.0)

    val attributes = Attributes()

    val editorPose: Pose? by lazy { Resources.instance.costumes.find(costumeName)?.editorPose() }

    val pose: Pose? by lazy {
        val costume = Resources.instance.costumes.find(costumeName)
        costume?.choosePose(costume.initialEventName)
    }

    val textStyle: TextStyle? by lazy {
        val costume = Resources.instance.costumes.find(costumeName)
        costume?.chooseTextStyle(costume.initialEventName)
    }

    var text: String = ""

    val displayText
        get() = if (text.isBlank()) "<no text>" else text

    var layer: StageLayer? = null

    init {
        if (isDesigning) {
            val costume = Resources.instance.costumes.find(costumeName)
            zOrder = costume?.zOrder ?: 0.0
        }
    }

    fun costume(): Costume? = Resources.instance.costumes.find(costumeName)

    fun isNinePatch(): Boolean {
        val costume = costume()
        return costume?.chooseNinePatch(costume.initialEventName) != null
    }

    fun createActor(): Actor? {
        val costume = costume()
        if (costume == null) {
            System.err.println("ERROR. Costume $costumeName not found in resources.")
            return null
        }
        val actor = costume.createActor(text)

        actor.x = x
        actor.y = y
        actor.zOrder = zOrder
        actor.direction.degrees = direction.degrees
        val appearance = actor.appearance
        if (appearance is NinePatchAppearance) {
            actor.resize(size.x, size.y)
        } else {
            actor.scale = scale
        }
        actor.xAlignment = xAlignment
        actor.yAlignment = yAlignment
        actor.flipX = flipX
        actor.flipY = flipY

        actor.role?.let { attributes.applyToObject(it) }
        return actor
    }

    private fun updateAttributesMetaData() {
        val roleString = Resources.instance.costumes.find(costumeName)?.roleString
        if (roleString != null && roleString.isNotBlank()) {
            attributes.updateAttributesMetaData(roleString, isDesigning)
        }
    }

    override fun toString() = "ActorResource $costumeName @ $x , $y direction=$direction.degrees"
}

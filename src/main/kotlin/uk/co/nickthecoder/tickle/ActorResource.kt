package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.util.Angle


/**
 * Details of an Actor's initial state.
 * Used when loading and editing a Scene. Not used during actual game play.
 */

class ActorResource(val isDesigning: Boolean = false) {

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

    override fun toString() = "ActorResource $costumeName @ $x , $y direction=$direction.degrees"
}

package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.stage.Stage

class Actor(val role: Role? = null) {

    init {
        role?.let {
            it.actor = this
        }
    }
    val stage: Stage? = null

    var x: Float = 0f

    var y: Float = 0f

    var z: Int = 0

    var appearance: Appearance = InvisibleAppearance()

}

package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource

interface HasTask {

    fun task(): Task

}

interface SnapTo : HasTask {

    fun snapActor(actorResource: DesignActorResource, adjustments: MutableList<Adjustment>)

    fun snapInfo() = "You can temporarily disable snapping by holding down the ctrl key while dragging."
}

data class Adjustment(var x: Double = 0.0, var y: Double = 0.0, var score: Double = Double.MAX_VALUE) {
    fun reset() {
        x = 0.0
        y = 0.0
        score = Double.MAX_VALUE
    }
}

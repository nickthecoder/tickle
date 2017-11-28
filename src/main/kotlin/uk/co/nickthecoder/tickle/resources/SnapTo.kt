package uk.co.nickthecoder.tickle.resources

interface SnapTo {

    fun snapActor(actorResource: ActorResource, adjustments: MutableList<Adjustment>)

    fun edit()
}

data class Adjustment(var x: Double = 0.0, var y: Double = 0.0, var score: Double = Double.MAX_VALUE) {
    fun reset() {
        x = 0.0
        y = 0.0
        score = Double.MAX_VALUE
    }
}

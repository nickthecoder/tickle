package uk.co.nickthecoder.tickle.action

class PeriodicFactory(
        val period: Float = 1f,
        val factory: (PeriodicFactory) -> Unit)

    : Action {

    var remainder: Float = 0f

    override fun tick() {
        remainder += 1
        while (remainder >= period) {
            remainder -= period
            factory(this)
        }
    }

}

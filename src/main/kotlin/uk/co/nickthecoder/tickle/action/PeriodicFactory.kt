package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class PeriodicFactory(
        val period: Float = 1f,
        var amount: Int? = null,
        val factory: (PeriodicFactory) -> Unit)

    : Action {

    var remainder: Float = 0f

    override fun act(actor: Actor): Boolean {
        remainder += 1
        while (remainder >= period) {
            remainder -= period
            factory(this)

            amount?.dec()
            if (amount == 0) {
                return true
            }
        }

        return false
    }

}

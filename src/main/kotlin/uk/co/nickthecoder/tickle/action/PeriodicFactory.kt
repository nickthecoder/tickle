package uk.co.nickthecoder.tickle.action

class PeriodicFactory<T>(
        val period: Double = 1.0,
        var amount: Int? = null,
        val factory: (PeriodicFactory<T>) -> Unit)

    : Action {

    var remainder: Double = 0.0

    override fun begin(): Boolean {
        return amount ?: 1 <= 0
    }

    override fun act(): Boolean {
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

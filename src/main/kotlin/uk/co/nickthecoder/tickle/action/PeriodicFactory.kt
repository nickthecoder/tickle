package uk.co.nickthecoder.tickle.action

class PeriodicFactory<T>(
        val period: Float = 1f,
        var amount: Int? = null,
        val factory: (PeriodicFactory<T>) -> Unit)

    : Action<T> {

    var remainder: Float = 0f

    override fun begin(target: T): Boolean {
        return amount ?: 1 <= 0
    }

    override fun act(target: T): Boolean {
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

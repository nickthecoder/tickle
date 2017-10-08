package uk.co.nickthecoder.tickle.action.animation


interface Ease {
    fun ease(t: Double): Double
}

class LinearEase : Ease {
    override fun ease(t: Double) = t

    companion object {
        val instance = LinearEase()
    }
}

package uk.co.nickthecoder.tickle.action.animation


interface Ease {
    fun ease(t: Float): Float
}

class LinearEase : Ease {
    override fun ease(t: Float) = t

    companion object {
        val instance = LinearEase()
    }
}

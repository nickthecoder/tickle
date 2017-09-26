package uk.co.nickthecoder.tickle.demo

interface Director {

    fun begin() {}

    fun preTick() {}

    fun postTick() {}

    fun end() {}

}

class NoDirector : Director {}

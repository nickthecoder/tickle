package uk.co.nickthecoder.tickle


abstract class AbstractRole : Role {

    override lateinit var actor: Actor

    override fun toString() = javaClass.simpleName

}

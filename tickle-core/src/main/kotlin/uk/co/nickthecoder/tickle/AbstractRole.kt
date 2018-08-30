package uk.co.nickthecoder.tickle


abstract class AbstractRole : Role {

    override lateinit var actor: Actor

    override fun activated() {}

    override fun begin() {}

    override fun end() {}

    override fun toString() = javaClass.simpleName

}

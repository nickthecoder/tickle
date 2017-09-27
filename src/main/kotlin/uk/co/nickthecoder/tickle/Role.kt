package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.action.Action
import java.util.concurrent.CopyOnWriteArrayList

interface Role {

    var actor: Actor

    fun begin() {}

    fun activated() {}

    fun tick()

    fun end() {}
}

abstract class AbstractRole : Role {

    override lateinit var actor: Actor

    val actions = CopyOnWriteArrayList<Action>()

    override fun tick() {
        actions.forEach { action ->
            action.tick()
        }
    }
}

class ActionsRole() : AbstractRole() {
    override fun tick() {
        super.tick()
    }
}

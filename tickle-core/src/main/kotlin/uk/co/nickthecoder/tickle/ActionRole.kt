package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction


/**
 * A Role that only has a single Action, and does nothing in the tick method itself.
 */
open class ActionRole : Role {

    private var finished: Boolean = false

    var action: Action = NoAction()
        set(v) {
            field = v
            finished = v.begin()
        }

    override lateinit var actor: Actor

    override fun begin() {}

    override fun end() {}

    override fun activated() {
        action = createAction()
    }

    open fun createAction(): Action = NoAction()

    override fun tick() {
        if (!finished) {
            finished = action.act()
        }
    }

    fun isFinished(): Boolean = finished
}

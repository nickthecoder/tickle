package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction


/**
 * A Role that only has a single Action, and does nothing in the tick method itself.
 */
open class ActionRole() : Role {

    private var activated: Boolean = false

    var action: Action = NoAction()
        set(v) {
            field = v
            if (activated) {
                v.begin()
            }
        }

    /**
     * If 'die' is true, then the Actor will be automatically killed when the Action ends.
     */
    constructor(action: Action, die: Boolean = true) : this() {
        if (die) {
            this.action = action
        } else {
            this.action = action.then(NoAction())
        }
    }

    override lateinit var actor: Actor

    override fun begin() {}

    override fun end() {}

    final override fun activated() {
        activated = true
        action = createAction() ?: NoAction()
        action.begin()
    }

    open fun createAction(): Action? = NoAction()

    final override fun tick() {
        if (action.act()) {
            actor.die()
        }
    }
}

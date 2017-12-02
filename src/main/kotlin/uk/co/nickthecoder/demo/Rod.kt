package uk.co.nickthecoder.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Resize

class Rod : ActionRole() {

    override fun createAction(): Action {
        return Resize(actor, 4.0, 140.0, 40.0)
                .then(Resize(actor, 4.0, 40.0, 240.0)).forever()
    }
}
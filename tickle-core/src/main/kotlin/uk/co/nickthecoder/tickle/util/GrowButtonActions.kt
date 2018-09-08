package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Scale

/**
 * This is merely an example of ButtonActions.
 * Get creative, and use other actions, such as changing color, rotating through 360°,
 * maybe the button should do a jiggle!
 */
class GrowButtonActions : ButtonActions {

    override fun downAction(button: Button): Action? {
        return Scale(button.actor, 0.1, 1.5, Eases.easeOut)
    }

    override fun upAction(button: Button): Action? {
        return Scale(button.actor, 0.1, 1.0, Eases.easeIn)
    }

}
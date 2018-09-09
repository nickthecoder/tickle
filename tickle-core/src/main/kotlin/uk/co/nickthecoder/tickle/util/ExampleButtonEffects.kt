package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.action.animation.Turn

/**
 * This is merely an example of ButtonActions.
 * It scales the actor, when the mouse hovers over the button, and shrinks again when it exits.
 * The click action rotates the button through 360 degrees.
 */
open class ExampleButtonEffects(val scale: Double) : ButtonEffects {

    constructor() : this(1.2)

    override fun enter(button: Button): Action? {
        return Scale(button.actor, 0.1, scale, Eases.easeOut)
    }

    override fun exit(button: Button): Action? {
        return Scale(button.actor, 0.1, 1.0, Eases.easeIn)
    }

    override fun clicked(button: Button): Action? {
        return Turn(button.actor.direction, 0.5, button.actor.direction + Angle.degrees(360.0), Eases.easeInOut)
    }

}
package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.TaggedRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.util.Tagged

abstract class Controllable : AbstractRole(), TaggedRole {

    var hasInput: Boolean = false

    override val tagged = Tagged(Game.instance.director.tagManager, this)

    var movement: Action<Actor> = NoAction()

    override fun begin() {
        tagged.add(Tags.CONTROLLABLE)
    }

    override fun activated() {
        movement.begin(actor)
    }

    override fun tick() {
        if (hasInput) {
            movement.act(actor)
        }
    }

    override fun end() {
        tagged.clear()
    }
}

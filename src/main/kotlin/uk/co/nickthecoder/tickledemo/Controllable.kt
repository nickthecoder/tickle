package uk.co.nickthecoder.tickledemo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.util.Tagged
import uk.co.nickthecoder.tickle.util.TaggedRole

abstract class Controllable : AbstractRole(), TaggedRole {

    var hasInput: Boolean = false

    override lateinit var tagged: Tagged

    var movement: Action = NoAction()

    override fun begin() {
        tagged = Tagged(Play.instance.tagManager, this)
        tagged.add(DemoTags.CONTROLLABLE)
    }

    override fun activated() {
        movement.begin()
    }

    override fun tick() {
        if (hasInput) {
            movement.act()
        }
    }

    override fun end() {
        tagged.clear()
    }
}

package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.movement.Follow

open class Follower(val follow: Actor, val offset: Vector2d)
    : ActionRole() {

    override fun createAction(): Action {
        return Follow(actor.position, follow, offset)
    }

}
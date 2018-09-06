package uk.co.nickthecoder.tickle.action.movement

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Angle

class FollowAngle(
        val follower: Angle,
        val following: Angle,
        val offsetRadians: Double = 0.0)

    : Action {

    constructor(
            follower: Angle,
            following: Angle,
            offset: Angle
    ) : this(follower, following, offset.radians)

    constructor(
            follower: Actor,
            following: Actor,
            offset: Angle
    ) : this(follower.direction, following.direction, offset.radians)

    constructor(
            follower: Actor,
            following: Actor
    ) : this(follower.direction, following.direction, 0.0)

    override fun act(): Boolean {
        follower.radians = following.radians + offsetRadians
        return false
    }
}
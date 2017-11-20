package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.resources.ActorResource

/**
 * If the zOrder is zero, then use the y value to order the actors, otherwise use the zOrder to order the actors.
 *
 * This is useful for isometric games, where some of the actors are "flying". The flying actors should set their zOrder
 * to an appropriate value (the y value if they were on the ground), and all land based actors have a zOrder of 0.0
 *
 * This implementation makes some things simple (e.g. smoke emanating from buildings - the zOrder of the smoke is
 * set to the building's y when first created), However for most isometric games, consider using [YMinusZStageView]
 * instead, and update the Actor's y and zOrder by the same amount when the actor's elevation changes.
 *
 * Note the "Offsets" of the Pose should be on the ground, at the center of the object.
 * For people, use the point between their feet (or the position of the grounded foot when walking/running).
 * For buildings use the middle of the building at ground level.
 * For flying objects, the same logic still applies, for example, a flying ball's offsets should be bottom middle of
 * the circle (i.e. Pose.width/2, Pose.height).
 * Do NOT use the center of the Pose's image as the Pose's offsets!
 */
class ZThenYStageView : AbstractStageView() {

    override fun orderActors(actorResources: List<ActorResource>, topFirst: Boolean): Iterable<ActorResource> {
        return if (topFirst) actorResources.sortedBy { if (it.zOrder == 0.0) it.y else it.zOrder } else actorResources.sortedBy { if (it.zOrder == 0.0) -it.y else -it.zOrder }
    }

    override fun orderedActors(topFirst: Boolean): Iterable<Actor> {
        return if (topFirst) stage.actors.sortedBy { if (it.zOrder == 0.0) it.y else it.zOrder } else stage.actors.sortedBy { if (it.zOrder == 0.0) -it.y else -it.zOrder }
    }

}

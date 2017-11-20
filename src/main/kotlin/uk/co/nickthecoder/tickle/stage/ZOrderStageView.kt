package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.resources.ActorResource

class ZOrderStageView
    : AbstractStageView() {

    override fun orderActors(actorResources: List<ActorResource>, topFirst: Boolean): Iterable<ActorResource> {
        direction

        return if (topFirst) actorResources.sortedBy { -it.zOrder } else actorResources.sortedBy { it.zOrder }
    }

    override fun orderedActors(topFirst: Boolean): Iterable<Actor> {
        return if (topFirst) stage.actors.sortedBy { -it.zOrder } else stage.actors.sortedBy { it.zOrder }
    }
}

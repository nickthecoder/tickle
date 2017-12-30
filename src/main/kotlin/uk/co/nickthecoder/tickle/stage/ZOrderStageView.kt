package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.resources.ActorResource

class ZOrderStageView
    : AbstractStageView() {

    override val comparator = Comparator<Actor> { o1, o2 ->
        Math.signum(o1.zOrder - o2.zOrder).toInt()
    }


    override fun orderActors(actorResources: List<ActorResource>, topFirst: Boolean): Iterable<ActorResource> {
        direction

        return if (topFirst) actorResources.sortedBy { -it.zOrder } else actorResources.sortedBy { it.zOrder }
    }

}

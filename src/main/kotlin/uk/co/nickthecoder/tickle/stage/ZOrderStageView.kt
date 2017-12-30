package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.ActorDetails

class ZOrderComparator : Comparator<ActorDetails> {
    override fun compare(o1: ActorDetails, o2: ActorDetails): Int = Math.signum(o1.zOrder - o2.zOrder).toInt()
}

class ZOrderStageView
    : AbstractStageView(ZOrderComparator()) {
}

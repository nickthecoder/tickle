package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.ActorDetails

class ZOrderStageView
    : AbstractStageView() {

    override val comparator = Comparator<ActorDetails> { o1, o2 ->
        Math.signum(o1.zOrder - o2.zOrder).toInt()
    }

}

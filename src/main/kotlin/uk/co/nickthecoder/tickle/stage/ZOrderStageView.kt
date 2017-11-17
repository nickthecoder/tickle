package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor

class ZOrderStageView
    : AbstractStageView() {

    override fun actorOrder(actor : Actor) = actor.zOrder

}

package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.ActorDetails

class YMinusZComparator : Comparator<ActorDetails> {
    override fun compare(o1: ActorDetails, o2: ActorDetails): Int = Math.signum(o1.y - o1.zOrder - (o2.y - o2.zOrder)).toInt()
}

/**
 * Useful for isometric games. For all ground based objects, set the Actor's zOrder to 0.0.
 * For flying objects, the Actor's y and zOrder must be changed together. As the actor rises upwards, increase both
 * y and zOrder by the same amount. Actor.y - Actor.zOrder is the object's actual y position. i.e. if you want to test if
 * one actor is hovering over another, you would compare their x's and also their 'y - zOrder'.
 *
 * Note the "Offsets" of the Pose should be on the ground, at the center of the object.
 * For people, use the point between their feet (or the position of the grounded foot when walking/running).
 * For buildings use the middle of the building at ground level.
 * For flying objects, the same logic still applies, for example, a flying ball's offsets should be bottom middle of
 * the circle (i.e. Pose.width/2, Pose.height).
 * Do NOT use the center of the Pose's image as the Pose's offsets!
 */
class YMinusZStageView
    : AbstractStageView(YMinusZComparator()) {
}
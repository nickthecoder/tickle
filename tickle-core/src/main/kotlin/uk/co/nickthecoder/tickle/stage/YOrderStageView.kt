package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.ActorDetails

class YOrderComparator : Comparator<ActorDetails> {
    override fun compare(o1: ActorDetails, o2: ActorDetails): Int = Math.signum(o1.y - o2.y).toInt()
}

/**
 * Orders Actors using their Y value only, use this for isometric games, where all Actors are touching the ground.
 * If you have above ground objects (flying objects or stacked objects), this simplistic view will NOT work correctly.
 * Consider using [YMinusZStageView] instead.
 *
 * Note, the Actor's zOrder is completely ignored.
 *
 * Note the "Offsets" of the Pose should be on the ground, at the center of the object.
 * For people, use the point between their feet (or the position of the grounded foot when walking/running).
 * For buildings use the middle of the building at ground level.
 * Do NOT use the center of the Pose's image as the Pose's offsets!
 */
class YOrderStageView
    : AbstractStageView(YOrderComparator()) {
}

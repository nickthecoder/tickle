package uk.co.nickthecoder.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.movement.FollowMouse
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.PixelOverlap
import uk.co.nickthecoder.tickle.resources.Resources

class Hand : ActionRole() {

    override fun createAction(): Action = FollowMouse(actor.position, actor.stage!!.firstView()!!)

    val overlapKey = Resources.instance.inputs.find("overlap")!!

    val pixelOverlap = PixelOverlap()

    override fun tick() {
        super.tick()

        if (overlapKey.isPressed()) {
            actor.stage?.actors?.forEach { other ->
                if (other !== actor) {
                    if (pixelOverlap.overlapping(other, actor)) {
                        actor.color = Color.red()
                        println("Hand is overlapping $other")
                    } else {
                        actor.color = Color.white()
                    }
                }
            }
        }
    }
}

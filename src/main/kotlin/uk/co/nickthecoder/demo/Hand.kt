package uk.co.nickthecoder.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.movement.FollowMouse
import uk.co.nickthecoder.tickle.collision.PixelOverlapping
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.resources.Resources

class Hand : ActionRole() {

    override fun createAction(): Action = FollowMouse(actor.position, actor.stage!!.firstView()!!)

    val overlapKey = Resources.instance.inputs.find("overlap")
    val touchingKey = Resources.instance.inputs.find("touching")

    val pixelOverlap = PixelOverlapping()

    override fun tick() {
        super.tick()

        if (touchingKey?.isPressed() == true) {
            actor.stage?.firstView()?.let { view ->
                println("\nActors at ${actor.position} = ${view.findActorsAt(actor.position).filter { it != actor }}\n\n")
            }
        }

        if (overlapKey?.isPressed() == true) {
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

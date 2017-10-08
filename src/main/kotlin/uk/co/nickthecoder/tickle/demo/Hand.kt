package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.movement.FollowMouse
import uk.co.nickthecoder.tickle.util.Attribute

class Hand : ActionRole() {

    /**
     *Just testing parameters!
     */

    @Attribute
    var myInt: Int = 0

    @Attribute
    var myDouble: Double = 0.0

    @Attribute(AttributeType.DIRECTION, order = 1)
    var myDirection: Double = 0.0

    @Attribute(AttributeType.DIRECTION, order = 2)
    var myOtherDirection: Double = 0.0

    @Attribute(AttributeType.ABSOLUTE_POSITION)
    var myAbsolute = Vector2d()

    @Attribute(AttributeType.RELATIVE_POSITION)
    var myRelative = Vector2d()

    override fun activated() {
        actor.stage?.firstView()?.let { view ->
            action = FollowMouse(actor.position, view)
        }
        super.activated()
    }

}

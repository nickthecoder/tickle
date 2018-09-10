package uk.co.nickthecoder.tickle.demo.action

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.EaseEnum
import uk.co.nickthecoder.tickle.action.movement.MoveBy
import uk.co.nickthecoder.tickle.util.Attribute

class MoveTest : TestVector() {

    @Attribute
    val amount = Vector2d()

    @Attribute
    var ease = EaseEnum.linear

    override fun createAction() =
            MoveBy(actor.position, amount, 3.0, ease)
                    .then(super.createAction())
}

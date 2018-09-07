package uk.co.nickthecoder.tickle.demo.overlapping

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.demo.TestDirector
import uk.co.nickthecoder.tickle.util.Attribute

class Moving : AbstractRole() {

    @Attribute
    var velocity = Vector2d()

    @Attribute
    var message: String = ""

    @Attribute
    var expected = Vector2d()

    override fun begin() {
        super.begin()
        (Game.instance.director as TestDirector).testCount++
    }

    override fun tick() {
        actor.position.add(velocity)

        val overlapping = (Game.instance.director as OverlappingDirector).overlapping
        actor.stage!!.findRolesByClass(Target::class.java).forEach { other ->

            if (overlapping.overlapping(actor, other.actor)) {
                (Game.instance.director as TestDirector).assertEquals(message, expected, actor.position)
                actor.role = null
            }

        }
    }
}
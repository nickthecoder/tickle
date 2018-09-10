package uk.co.nickthecoder.tickle.demo.action

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Do
import uk.co.nickthecoder.tickle.demo.TestDirector
import uk.co.nickthecoder.tickle.util.Attribute

open class TestVector : ActionRole() {

    @Attribute
    var message = ""

    @Attribute
    var expected = Vector2d()


    override fun begin() {
        super.begin()
        (Game.instance.director as TestDirector).testCount++
    }

    override fun createAction(): Action = Do {
        (Game.instance.director as TestDirector).assertEquals(message, expected, actor.position)
    }

}
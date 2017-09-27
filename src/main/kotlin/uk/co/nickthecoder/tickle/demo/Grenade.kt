package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.AcceleratedXYMovement
import uk.co.nickthecoder.tickle.action.Die
import uk.co.nickthecoder.tickle.action.Fade
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.math.Vector2
import java.util.*

class Grenade : AbstractRole() {

    val random = Random()

    override fun activated() {
        actions.add(PeriodicFactory(10f) {

            val newRole = ActionRole(
                    AcceleratedXYMovement(
                            Vector2(random.nextFloat() * 0.1f - 0.05f, 7f), 1f,
                            Vector2(random.nextFloat() * 0.02f - 0.01f, -0.1f + random.nextFloat() * 0.01f)
                    ).and(
                            Fade(Color.TRANSPARENT_WHITE, 4f).then(Die())
                    )
            )
            val newActor = Actor(newRole)

            newActor.x = actor.x
            newActor.y = actor.y
            newActor.z = actor.z - 1
            actor.stage?.add(newActor)
            newActor.changePose(Resources.instance.sparkPose)

        })
    }

}

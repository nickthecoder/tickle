package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.ActionsRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.AcceleratedXYMovement
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.math.Vector2
import java.util.*

class Grenade : AbstractRole() {

    val random = Random()

    override fun activated() {
        actions.add(PeriodicFactory(10f) {
            val newRole = ActionsRole()
            val newActor = Actor(newRole)
            val action = AcceleratedXYMovement(
                    newActor,
                    Vector2(0f, 10f), 1f,
                    Vector2(random.nextFloat() * 0.01f - 0.005f, -0.1f + random.nextFloat() * 0.01f))

            newActor.x = actor.x
            newActor.y = actor.y
            newActor.z = actor.z - 1
            actor.stage?.add(newActor)
            newActor.changePose(Resources.instance.sparkPose)
            newRole.actions.add(action)
        })
    }

}

package uk.co.nickthecoder.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle


class Eye : AbstractRole() {

    lateinit var pupilA: Actor

    val angle = Angle()

    override fun activated() {
        pupilA = actor.createChildOnStage("pupil")
        pupilA.zOrder += 1
    }

    override fun tick() {
        actor.stage?.firstView()?.let { view ->
            angle.of(view.mousePosition().sub(actor.position))
            pupilA.position.set(actor.position).add(angle.vector().mul(5.0, 10.0))
        }
    }
}

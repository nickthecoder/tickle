package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle


class Eye : AbstractRole() {

    lateinit var pupilA: Actor

    val angle = Angle()

    private val mouse = Vector2d()

    override fun activated() {
        pupilA = actor.createChildOnStage("pupil")
    }

    override fun tick() {
        actor.stage?.firstView()?.let { view ->
            view.mousePosition(mouse)
            mouse.sub(actor.position)
            angle.of(mouse)
            pupilA.position.set(actor.position).add(angle.vector().mul(5.0, 10.0))
        }
    }
}

package uk.co.nickthecoder.tickle.demo.physics

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.physics.TickleMouseJoint
import uk.co.nickthecoder.tickle.stage.StageView

class PhysicsDirector : AbstractDirector() {

    var mouse = Vector2d()

    var mouseJoint: TickleMouseJoint? = null

    lateinit var ground: Actor

    override fun activated() {
        super.activated()
        ground = Game.instance.scene.stages().firstOrNull()!!.findRoleByClass(Ground::class.java)!!.actor
    }

    fun updateMouse() {
        Game.instance.scene.views().firstOrNull()?.mousePosition(mouse)
    }

    override fun onMouseButton(event: MouseEvent) {
        updateMouse()
        if (event.button == 0) {
            if (event.state == ButtonState.PRESSED) {
                Game.instance.scene.views().firstOrNull()?.let { view ->
                    if (view is StageView) {
                        view.stage.findActorAt(mouse)?.let { actor ->
                            mouseJoint = TickleMouseJoint(actor, mouse, 10.0, ground)
                        }

                    }
                }
            } else {
                mouseJoint?.destroy()
                mouseJoint = null
            }
        }
    }

    override fun tick() {
        mouseJoint?.let {
            updateMouse()
            it.target(mouse)
        }

    }
}

/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.demo


import org.jbox2d.common.Vec2
import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.action.movement.polar.*
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Polar2d

class Bee : Controllable() {

    val velocity = Polar2d()

    val ejectNoBounce = Resources.instance.inputs.find("ejectNoBounce")
    val ejectFind = Resources.instance.inputs.find("ejectFind")
    val ejectTagged = Resources.instance.inputs.find("ejectTagged")
    val ejectNeighbourhood = Resources.instance.inputs.find("ejectNeighbourhood")
    val ejectWorld = Resources.instance.inputs.find("ejectWorld")

    var flipped = false

    override fun activated() {

        val turn = GradualTurnInput(velocity.angle, Angle.degrees(1.0), Angle.degrees(5.0), drag = 0.07)
                .and(ChangeDirection(actor, velocity.angle))

        val forwards = AcceleratePolarInput(velocity, 0.2)
                .and(LimitSpeed(velocity, 10.0))
                .and(MovePolar(actor.position, velocity))

        movement = turn.and(forwards)
        actor.color = Color(1f, 1f, 1f, 0.5f)
        super.activated()
    }

    override fun tick() {
        super.tick()

        // Flip the image so that the bee never appears to fly upside down.
        var angle = actor.direction.degrees.rem(360.0)
        if (angle < 0) {
            // "rem" can return positive and negative numbers, so adjust the negative numbers, so they become positive.
            angle = 360 + angle
        }
        if (flipped) {
            if (angle > 90 && angle < 270) {
                flipped = false
                actor.event("flipped")
            }
        } else {
            if (angle < 90 || angle > 270) {
                flipped = true
                actor.event("default")
            }

        }

        if (ejectNoBounce?.isPressed() == true) {
            eject(NoBounce())
        }
        if (ejectFind?.isPressed() == true) {
            eject(FindBounce())
        }
        if (ejectTagged?.isPressed() == true) {
            eject(TaggedBounce())
        }
        if (ejectNeighbourhood?.isPressed() == true) {
            eject(NeighbourhoodBounce())
        }
        if (ejectWorld?.isPressed() == true) {
            val newActor = actor.createChildOnStage("ejectWorld")
            newActor.body?.let { body ->
                body.linearVelocity = Vec2(-3f, -3f)
            }
        }
    }

    fun eject(role: Bounce) {
        val bouncyA = Actor(Costume(), role)
        bouncyA.changeAppearance(Resources.instance.poses.find("coin")!!)
        bouncyA.position.x = actor.x
        bouncyA.position.y = actor.y
        role.velocity.x = Math.cos(actor.direction.radians) * 10.0
        role.velocity.y = Math.sin(actor.direction.radians) * 10.0
        actor.stage?.add(bouncyA)

    }

    companion object {
        val beeAngle = 24
        // A reflection matrix through a plane cutting through the middle of the bee's body.
        // This assumes that (0,0) is in the middle of the bee.
        // These number are the cos and sin of the angle above (roughly).
        val flipMatrix = Matrix4f().reflect(-0.406f, 0.914f, 0f, 0f)
    }

}

package uk.co.nickthecoder.tickle.demo


import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.action.movement.polar.*
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Polar2d

class Bee : Controllable() {

    val velocity = Polar2d()

    val ejectNoBounce = Resources.instance.input("ejectNoBounce")
    val ejectSimple = Resources.instance.input("ejectSimple")
    val ejectNeighbourhood = Resources.instance.input("ejectNeighbourhood")

    override fun activated() {

        val turn = GradualTurnInput(velocity.angle, Angle.degrees(1.0), Angle.degrees(5.0), drag = 0.07)
                .and(ChangeDirection(actor, velocity.angle))

        val forwards = AcceleratePolarInput(velocity, 0.2)
                .and(LimitSpeed(velocity, 10.0))
                .and(MovePolar(actor.position, velocity))

        movement = turn.and(forwards)
        super.activated()
    }

    override fun tick() {
        super.tick()

        // Flip the image so that the bee never appears to fly upside down.
        // The bee image is tilted upwards (by about 24 degrees), so we can't use Actor.flipX().
        // Instead, we need to use a custom transformation, which reflect the image
        // along the plane through the middle of the bee's body.
        var angle = actor.direction.degrees.rem(360.0)
        if (angle < 0) {
            // "rem" can return positive and negative numbers, so adjust the negative numbers, so they become positive.
            angle = 360 + angle
        }
        actor.customTransformation = if (angle > 90 && angle < 270) flipMatrix else null

        if (ejectNoBounce.isPressed()) {
            eject(NoBounce())
        }
        if (ejectSimple.isPressed()) {
            eject(SimpleBounce())
        }
        if (ejectNeighbourhood.isPressed()) {
            eject(NeighbourhoodBounce())
        }
    }

    fun eject(role: Bounce) {
        val bouncyA = Actor(Costume(),role)
        bouncyA.changeAppearance(Resources.instance.pose("coin"))
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

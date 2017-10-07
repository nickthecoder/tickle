package uk.co.nickthecoder.tickle.demo


import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Heading
import uk.co.nickthecoder.tickle.util.Scalar
import uk.co.nickthecoder.tickle.action.movement.polar.*

class Bee : Controllable() {

    val speed = Scalar()

    val heading = Heading()

    override fun activated() {
        val turn = GradualTurnInput<Actor>(heading, 1.0, 5.0, drag = 0.07)
                .and(ApplyHeading(heading))
        val forwards = AcceleratePolarInput(speed, 0.2f).and(LimitSpeed(speed, 10f)).and(MovePolar(heading, speed))
        movement = turn.and(forwards)
        /*
        movement = DirectionControls(
                maxSpeed = 10f,
                speedChange = 0.2f,
                maxRotationSpeed = 5.0,
                rotationDrag = 0.07)
                */
        super.activated()
    }

    override fun tick() {
        super.tick()

        // Flip the image so that the bee never appears to fly upside down.
        // The bee image is tilted upwards (by about 24 degrees), so we can't use Actor.flipX().
        // Instead, we need to use a custom transformation, which reflect the image
        // along the plane through the middle of the bee's body.
        var angle = actor.directionDegrees.rem(360.0)
        if (angle < 0) {
            // "rem" can return positive and negative numbers, so adjust the negative numbers, so they become positive.
            angle = 360 + angle
        }
        actor.customTransformation = if (angle > 90 && angle < 270) flipMatrix else null
    }

    companion object {
        val beeAngle = 24
        // A reflection matrix through a plane cutting through the middle of the bee's body.
        // This assumes that (0,0) is in the middle of the bee.
        // These number are the cos and sin of the angle above (roughly).
        val flipMatrix = Matrix4f().reflect(-0.406f, 0.914f, 0f, 0f)
    }

}

package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Role

abstract class AbstractHeadingMovement(
        speed: Float = 0f,
        speedDegrees: Double = 0.0,
        var maxSpeed: Float = 10f,
        var minSpeed: Float = -maxSpeed,
        maxRotationDegrees: Double = 10.0)

    : Action<Actor> {

    var speed: Float = speed
        set(v) {
            if (v > maxSpeed) {
                field = maxSpeed
            } else if (v < minSpeed) {
                field = minSpeed
            } else {
                field = v
            }
        }

    var maxRotationRadians = Math.toRadians(maxRotationDegrees)

    var maxRotationDegrees: Double
        get() = Math.toDegrees(maxRotationRadians)
        set(v) {
            maxRotationRadians = Math.toRadians(v)
        }

    var speedRadians = Math.toRadians(speedDegrees)
        set(v) {
            if (v > maxRotationRadians) {
                field = maxRotationRadians
            } else if (v < -maxRotationRadians) {
                field = -maxRotationRadians
            } else {
                field = v
            }
        }

    var speedDegrees: Double
        get() = Math.toDegrees(speedRadians)
        set(v) {
            speedRadians = Math.toRadians(v)
        }


    override fun act(target: Actor): Boolean {

        headingRadians += speedRadians

        if (speed != 0f) {
            val radians = Math.toRadians(headingDegrees)
            val cos = Math.cos(radians).toFloat()
            val sin = Math.sin(radians).toFloat()

            target.x += cos * speed
            target.y += sin * speed
        }

        return false
    }

    abstract var headingRadians: Double

    var headingDegrees: Double
        get() = Math.toDegrees(headingRadians)
        set(v) {
            headingRadians = Math.toRadians(v)
        }
}

class HeadingMovement(
        speed: Float = 0f,
        headingDegrees: Double = 0.0,
        speedDegrees: Double = 0.0,
        maxSpeed: Float = 10f,
        minSpeed: Float = -maxSpeed,
        maxRotationDegrees: Double = 10.0)

    : AbstractHeadingMovement(
        speed = speed,
        speedDegrees = speedDegrees,
        maxSpeed = maxSpeed,
        minSpeed = minSpeed,
        maxRotationDegrees = maxRotationDegrees) {


    override var headingRadians = Math.toRadians(headingDegrees)

}

open class DirectionMovement(
        val role: Role,
        speed: Float = 0f,
        speedDegrees: Double = 0.0,
        maxSpeed: Float = 10f,
        minSpeed: Float = -maxSpeed,
        maxRotationDegrees: Double)

    : AbstractHeadingMovement(
        speed = speed,
        speedDegrees = speedDegrees,
        maxSpeed = maxSpeed,
        minSpeed = minSpeed,
        maxRotationDegrees = maxRotationDegrees) {

    override var headingRadians: Double
        get() = role.actor.directionRadians
        set(v) {
            role.actor.directionRadians = v
        }
}

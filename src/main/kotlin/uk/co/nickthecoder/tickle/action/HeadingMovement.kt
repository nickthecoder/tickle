package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.math.toDegrees
import uk.co.nickthecoder.tickle.math.toRadians

class HeadingMovement(
        speed: Float = 0f,
        headingDegrees: Double = 0.0,
        speedDegrees: Double = 0.0,
        var maxSpeed: Float = 10f,
        var minSpeed: Float = -maxSpeed,
        maxRotationDegrees: Double = 10.0)

    : Action {

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


    private var headingRadians = toRadians(headingDegrees)

    var headingDegrees: Double
        get() = toDegrees(headingRadians)
        set(v) {
            headingRadians = toRadians(v)
        }

    private var maxRotationRadians = toRadians(maxRotationDegrees)

    var maxRotationDegrees: Double
        get() = toDegrees(maxRotationRadians)
        set(v) {
            maxRotationRadians = toRadians(v)
        }

    private var speedRadians = toRadians(speedDegrees)
        set(v) {
            if (v > maxRotationRadians) {
                field = toRadians(maxRotationRadians)
            } else if (v < -maxRotationRadians) {
                field = toRadians(-maxRotationRadians)
            } else {
                field = toRadians(v)
            }
        }

    var speedDegrees: Double
        get() = toDegrees(speedRadians)
        set(v) {
            speedRadians = toRadians(v)
        }


    override fun act(actor: Actor): Boolean {

        headingDegrees += speedDegrees

        if (speed != 0f) {
            val radians = toRadians(headingDegrees)
            val cos = Math.cos(radians).toFloat()
            val sin = Math.sin(radians).toFloat()

            actor.x += cos * speed
            actor.y += sin * speed
        }

        return false
    }

}

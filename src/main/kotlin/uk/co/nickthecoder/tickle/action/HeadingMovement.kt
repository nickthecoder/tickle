package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

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


    private var headingRadians = Math.toRadians(headingDegrees)

    var headingDegrees: Double
        get() = Math.toDegrees(headingRadians)
        set(v) {
            headingRadians = Math.toRadians(v)
        }

    private var maxRotationRadians = Math.toRadians(maxRotationDegrees)

    var maxRotationDegrees: Double
        get() = Math.toDegrees(maxRotationRadians)
        set(v) {
            maxRotationRadians = Math.toRadians(v)
        }

    private var speedRadians = Math.toRadians(speedDegrees)
        set(v) {
            if (v > maxRotationRadians) {
                field = Math.toRadians(maxRotationRadians)
            } else if (v < -maxRotationRadians) {
                field = Math.toRadians(-maxRotationRadians)
            } else {
                field = Math.toRadians(v)
            }
        }

    var speedDegrees: Double
        get() = Math.toDegrees(speedRadians)
        set(v) {
            speedRadians = Math.toRadians(v)
        }


    override fun act(actor: Actor): Boolean {

        headingDegrees += speedDegrees

        if (speed != 0f) {
            val radians = Math.toRadians(headingDegrees)
            val cos = Math.cos(radians).toFloat()
            val sin = Math.sin(radians).toFloat()

            actor.x += cos * speed
            actor.y += sin * speed
        }

        return false
    }

}

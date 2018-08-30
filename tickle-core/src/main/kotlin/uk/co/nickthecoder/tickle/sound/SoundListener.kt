package uk.co.nickthecoder.tickle.sound

import org.joml.Vector3f
import org.lwjgl.openal.AL10

object SoundListener {

    fun position(position: Vector3f) {
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z)
    }

    fun velocity(velocity: Vector3f) {
        AL10.alListener3f(AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z)
    }

    fun direction(direction: Vector3f) {
        AL10.alListener3f(AL10.AL_ORIENTATION, direction.x, direction.y, direction.z)
    }

    fun gain(gain: Float) {
        AL10.alListenerf(AL10.AL_GAIN, gain)
    }

}

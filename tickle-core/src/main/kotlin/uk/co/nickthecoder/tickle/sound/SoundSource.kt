package uk.co.nickthecoder.tickle.sound

import org.joml.Vector3f
import org.lwjgl.openal.AL10


class SoundSource {

    val handle: Int = AL10.alGenSources()

    fun loop(loop: Boolean) {
        AL10.alSourcei(handle, AL10.AL_LOOPING, if (loop) AL10.AL_TRUE else AL10.AL_FALSE)
    }

    fun relative(relative: Boolean) {
        AL10.alSourcei(handle, AL10.AL_SOURCE_RELATIVE, if (relative) AL10.AL_TRUE else AL10.AL_FALSE)
    }

    fun position(position: Vector3f) {
        AL10.alSource3f(handle, AL10.AL_POSITION, position.x, position.y, position.z)
    }

    fun velocity(position: Vector3f) {
        AL10.alSource3f(handle, AL10.AL_VELOCITY, position.x, position.y, position.z)
    }

    fun bind(sound: Sound) {
        AL10.alSourcei(handle, AL10.AL_BUFFER, sound.handle)
    }

    fun play() {
        AL10.alSourcePlay(handle)
    }

    fun play(sound: Sound) {
        bind(sound)
        play()
    }

    fun gain(gain: Float) {
        AL10.alSourcef(handle, AL10.AL_GAIN, gain)
    }


    fun isPlaying(): Boolean {
        return AL10.alGetSourcei(handle, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING
    }

    fun pause() {
        AL10.alSourcePause(handle)
    }

    fun stop() {
        AL10.alSourceStop(handle)
    }

    fun cleanUp() {
        stop()
        AL10.alDeleteSources(handle)
    }
}

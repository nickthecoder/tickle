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

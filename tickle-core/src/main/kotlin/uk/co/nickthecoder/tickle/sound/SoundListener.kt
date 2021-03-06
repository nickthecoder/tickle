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

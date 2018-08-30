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

import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10
import org.lwjgl.openal.ALCCapabilities
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.ByteBuffer
import java.nio.IntBuffer


object SoundManager {

    private var _device: Long = 0L

    private var _capabilities: ALCCapabilities? = null

    private var _context: Long = 0L

    val device: Long
        get() = _device

    val capabilities: ALCCapabilities
        get() = _capabilities!!

    val context: Long
        get() = _context

    private val MAX_SOUNDS = 10

    private val sources = mutableListOf<SoundSource>()

    fun initialise() {

        if (_device != 0L) {
            System.err.println("WARNING. SoundManager has already been initialised.")
            return
        }

        _device = ALC10.alcOpenDevice(null as ByteBuffer?)
        if (_device == NULL) {
            throw IllegalStateException("Failed to open the default OpenAL device.")
        }
        _capabilities = ALC.createCapabilities(_device)
        _context = ALC10.alcCreateContext(_device, null as IntBuffer?)
        if (_context == NULL) {
            throw IllegalStateException("Failed to create OpenAL context.")
        }
        ALC10.alcMakeContextCurrent(_context)
        AL.createCapabilities(_capabilities)

        for (i in 0..MAX_SOUNDS) {
            sources.add(SoundSource())
        }
    }

    fun ensureInitialised() {
        if (_device == 0L) {
            initialise()
        }
    }

    private fun findFreeSource(): SoundSource? = sources.firstOrNull { !it.isPlaying() }

    fun play(sound: Sound) {
        findFreeSource()?.play(sound)
    }

    fun cleanUp() {
        if (_context != 0L) {
            ALC10.alcDestroyContext(_context)
            _context = 0L
        }
        if (_device != 0L) {
            ALC10.alcCloseDevice(_device)
            _device = 0L
        }
    }

}

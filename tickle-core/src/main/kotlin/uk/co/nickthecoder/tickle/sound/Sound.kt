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

import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL10
import org.lwjgl.stb.STBVorbis.*
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryUtil.NULL
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files


class Sound {

    init {
        SoundManager.ensureInitialised()
    }

    val handle: Int = AL10.alGenBuffers()

    val file: File?

    constructor() {
        file = null
    }

    constructor(file: File) {
        this.file = file

        reload()
    }

    fun reload() {
        file?.let {
            when (file.extension) {
                "ogg" -> readOgg(file)
            // TODO Support wav files too (and update soundP."extensions" in NewResourcesTask).
                else -> throw IllegalArgumentException("Only ogg files supported.")
            }
        }
    }

    private fun readOgg(file: File) {

        STBVorbisInfo.malloc().use { info ->

            val ogg = readFileIntoBuffer(file)

            val error = BufferUtils.createIntBuffer(1)
            val decoder = stb_vorbis_open_memory(ogg, error, null)
            if (decoder == NULL) {
                throw RuntimeException("Error loading Ogg file $file. " + error.get(0))
            }

            stb_vorbis_get_info(decoder, info)

            val channels = info.channels()
            val lengthSamples = stb_vorbis_stream_length_in_samples(decoder)
            val pcm = BufferUtils.createShortBuffer(lengthSamples)

            pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels)
            stb_vorbis_close(decoder)

            AL10.alBufferData(handle, if (info.channels() == 1) AL10.AL_FORMAT_MONO16 else AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate())
        }
    }

    private fun readFileIntoBuffer(file: File): ByteBuffer {

        val path = file.toPath()
        Files.newByteChannel(path).use { byteChannel ->
            val buffer = BufferUtils.createByteBuffer(byteChannel.size().toInt() + 1)
            while (byteChannel.read(buffer) != -1) {
            }
            buffer.flip()
            return buffer
        }
    }

    fun cleanUp() {
        AL10.alDeleteBuffers(handle)
    }

    override fun toString() = "Sound : file=$file"
}

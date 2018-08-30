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
package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL15.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class VertexBuffer {

    val handle: Int = glGenBuffers()

    fun bind(target: Target) {
        glBindBuffer(target.value, handle)
    }

    fun delete() {
        glDeleteBuffers(handle)
    }

    fun uploadData(target: Target, data: FloatBuffer, usage: Usage) {
        glBufferData(target.value, data, usage.value)
    }

    fun uploadData(target: Target, size: Long, usage: Usage) {
        glBufferData(target.value, size, usage.value)
    }

    fun uploadSubData(target: Target, offset: Long, data: FloatBuffer) {
        glBufferSubData(target.value, offset, data)
    }

    fun uploadData(target: Target, data: IntBuffer, usage: Usage) {
        glBufferData(target.value, data, usage.value)
    }

}
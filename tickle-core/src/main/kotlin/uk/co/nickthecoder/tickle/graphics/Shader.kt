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


import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*
import java.io.File
import java.io.InputStream

class Shader(type: ShaderType, source: CharSequence) {

    val handle = glCreateShader(type.value)

    init {
        glShaderSource(handle, source)
        glCompileShader(handle)

        val status = glGetShaderi(handle, GL_COMPILE_STATUS)
        if (status != GL_TRUE) {
            throw RuntimeException(glGetShaderInfoLog(handle))
        }
        // println("Shader compiled Ok")
    }

    fun delete() {
        glDeleteShader(handle)
    }

    companion object {

        fun load(type: ShaderType, file: File): Shader {
            return Shader(type, file.readText())
        }

        fun load(type: ShaderType, input: InputStream): Shader {
            return Shader(type, input.bufferedReader().use { it.readText() })
        }
    }

}

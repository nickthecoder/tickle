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

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindFragDataLocation
import org.lwjgl.system.MemoryStack

public class ShaderProgram() {

    fun attachShaders(vararg shaders: Shader) {
        shaders.forEach { shader ->
            glAttachShader(handle, shader.handle)
        }
    }

    val handle = glCreateProgram()

    fun bindFragmentDataLocation(number: Int, name: CharSequence) {
        glBindFragDataLocation(handle, number, name);
    }

    fun link() {
        glLinkProgram(handle)
        checkStatus();
        //println("Shader Program Linked OK")
    }


    fun getAttributeLocation(name: CharSequence): Int = glGetAttribLocation(handle, name)

    fun enableVertexAttribute(index: Int) {
        glEnableVertexAttribArray(index);
    }

    fun disableVertexAttribute(index: Int) {
        glDisableVertexAttribArray(index);
    }

    fun pointVertexAttribute(index: Int, size: Int, stride: Int, offset: Long) {
        glVertexAttribPointer(index, size, GL_FLOAT, false, stride, offset);
    }

    fun getUniformLocation(name: CharSequence): Int = glGetUniformLocation(handle, name)


    fun setUniform(location: Int, value: Int) {
        glUniform1i(location, value);
    }

    fun setUniform(location: Int, value: Matrix4f) {
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(4 * 4)
            value[0, buffer]
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    fun setUniform(location: Int, value: Color) {
        glUniform4f(location, value.red, value.green, value.blue, value.alpha)
    }

    fun use() {
        glUseProgram(handle)
    }

    fun checkStatus() {
        val status = glGetProgrami(handle, GL_LINK_STATUS)
        if (status != GL_TRUE) {
            throw RuntimeException(glGetProgramInfoLog(handle))
        }
    }

    fun delete() {
        glDeleteProgram(handle)
    }

}

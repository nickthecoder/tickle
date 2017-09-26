package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindFragDataLocation
import org.lwjgl.system.MemoryStack
import uk.co.nickthecoder.tickle.math.Matrix4

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
        println("Shader Program Linked OK")
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

    fun setUniform(location: Int, value: Matrix4) {
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(4 * 4)
            value.intoBuffer(buffer);
            glUniformMatrix4fv(location, false, buffer);
        }
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

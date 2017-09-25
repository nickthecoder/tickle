package uk.co.nickthecoder.tickle.graphics


import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*
import java.io.File

class Shader(type: ShaderType) {

    val handle = glCreateShader(type.value)

    fun source(source: CharSequence) {
        glShaderSource(handle, source)
    }

    fun compile() {
        glCompileShader(handle)
        checkStatus()
        println("Shader compiled Ok")
    }

    /**
     * Checks if the shader was compiled successfully.
     */
    private fun checkStatus() {
        val status = glGetShaderi(handle, GL_COMPILE_STATUS)
        if (status != GL_TRUE) {
            throw RuntimeException(glGetShaderInfoLog(handle))
        }
    }

    fun delete() {
        glDeleteShader(handle)
    }

    companion object {

        fun create(type: ShaderType, source: CharSequence): Shader {
            val shader = Shader(type)
            shader.source(source)
            shader.compile()

            return shader
        }

        fun load(type: ShaderType, file: File): Shader {
            return create(type, file.readText())
        }
    }

}

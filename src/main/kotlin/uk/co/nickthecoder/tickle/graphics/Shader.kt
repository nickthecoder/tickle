package uk.co.nickthecoder.tickle.graphics


import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*
import java.io.File

class Shader(type: ShaderType, source: CharSequence) {

    val handle = glCreateShader(type.value)

    init {
        println("Compiling shader :\n${source}")
        glShaderSource(handle, source)
        glCompileShader(handle)

        val status = glGetShaderi(handle, GL_COMPILE_STATUS)
        if (status != GL_TRUE) {
            throw RuntimeException(glGetShaderInfoLog(handle))
        }
        println("Shader compiled Ok")
    }

    fun delete() {
        glDeleteShader(handle)
    }

    companion object {

        fun load(type: ShaderType, file: File): Shader {
            return Shader(type, file.readText())
        }
    }

}

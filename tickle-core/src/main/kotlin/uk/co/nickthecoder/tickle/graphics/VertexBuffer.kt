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
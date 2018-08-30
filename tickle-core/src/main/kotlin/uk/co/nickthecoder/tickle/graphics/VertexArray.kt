package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL30.*

class VertexArray {

    val handle: Int = glGenVertexArrays()

    fun bind() {
        glBindVertexArray(handle)
    }

    fun delete() {
        glDeleteVertexArrays(handle)
    }

}

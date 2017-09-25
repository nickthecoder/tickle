package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL15

enum class Target(val value: Int) {

    ARRAY_BUFFER(GL15.GL_ARRAY_BUFFER),

    ELEMENT_ARRAY_BUFFER(GL15.GL_ELEMENT_ARRAY_BUFFER)

}

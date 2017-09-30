package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil

/**
 * Creates a GL context without displaying a window.
 * I use this in the Editor, so that the Textures etc can be loaded.
 */
class WindowlessContext {

    val handle: Long

    init {

        GLFWErrorCallback.createPrint(System.err).set()

        if (!GLFW.glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        // Create a glfx context, so that the resources will load.
        handle = GLFW.glfwCreateWindow(10, 10, "Tickle Dummy Window", MemoryUtil.NULL, MemoryUtil.NULL)
        GLFW.glfwMakeContextCurrent(handle)

        GL.createCapabilities()

    }

    fun delete() {
        GLFW.glfwDestroyWindow(handle)
    }
}
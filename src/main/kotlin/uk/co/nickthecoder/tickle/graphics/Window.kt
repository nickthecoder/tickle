package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.events.KeyEvent

class Window(
        title: String,
        val width: Int,
        val height: Int,
        resizable: Boolean = false) {

    val handle: Long

    init {

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, if (resizable) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)

        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (handle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }
    }

    // TODO If the window is resizeable, then val width and val height will NOT be correct. Need sometimg like this :
    /*
        /* Get width and height of framebuffer */
        var width: Int = 0
        var height: Int = 0
        MemoryStack.stackPush().use { stack ->
            val widthBuffer = stack.mallocInt(1)
            val heightBuffer = stack.mallocInt(1)
            GLFW.glfwGetFramebufferSize(window.handle, widthBuffer, heightBuffer)
            width = widthBuffer.get()
            height = heightBuffer.get()
        }

     */

    fun keyboardEvents(keyHandler: (KeyEvent) -> Unit) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(handle) { _, key, scanCode, action, mods ->
            keyHandler(KeyEvent(this, key, scanCode, action, mods))
        }
    }

    fun close() {
        GLFW.glfwSetWindowShouldClose(handle, true)
    }

    fun show() {
        // Get the thread stack and push a new frame
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(handle, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

            // Center the window
            GLFW.glfwSetWindowPos(
                    handle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            )
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(handle)

        // Make the window visible
        GLFW.glfwShowWindow(handle)
    }

    fun enableVSync(interval: Int = 1) {
        GLFW.glfwSwapInterval(interval)
    }

    fun shouldClose(): Boolean = GLFW.glfwWindowShouldClose(handle)

    fun swap() {
        GLFW.glfwSwapBuffers(handle)
    }

    fun delete() {
        Callbacks.glfwFreeCallbacks(handle)
        GLFW.glfwDestroyWindow(handle)
    }
}

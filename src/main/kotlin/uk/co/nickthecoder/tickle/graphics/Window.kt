package uk.co.nickthecoder.tickle.graphics

import org.joml.Vector2d
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.Key
import uk.co.nickthecoder.tickle.events.KeyEvent

class Window(
        title: String,
        private var _width: Int,
        private var _height: Int,
        resizable: Boolean = false) {

    val handle: Long

    val width: Int
        get() = _width

    val height: Int
        get() = _height

    init {

        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        handle = glfwCreateWindow(_width, _height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (handle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }
        glfwMakeContextCurrent(handle)
        GL.createCapabilities()
    }


    fun keyboardEvents(keyHandler: (KeyEvent) -> Unit) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(handle) { _, keyCode, scanCode, action, mods ->
            keyHandler(KeyEvent(this, Key.forCode(keyCode), scanCode, ButtonState.of(action), mods))
        }
    }

    fun showMouse( value : Boolean = true ) {
        glfwSetInputMode(handle, GLFW_CURSOR, if (value) GLFW_CURSOR_NORMAL else GLFW_CURSOR_HIDDEN)
    }

    fun close() {
        if (current === this) {
            current = null
        }
        glfwSetWindowShouldClose(handle, true)
    }

    fun show() {
        current = this

        glfwSetWindowShouldClose(handle, false)

        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(handle, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                    handle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            )
        }

        // Make the OpenGL context instance
        glfwMakeContextCurrent(handle)

        // Make the window visible
        glfwShowWindow(handle)

        val resizeCallback = object : GLFWWindowSizeCallback() {
            override fun invoke(window: Long, newWidth: Int, newHeight: Int) {
                _width = newWidth
                _height = newHeight
            }
        }
        glfwSetWindowSizeCallback(handle, resizeCallback)
    }

    fun hide() {
        glfwHideWindow(handle)
    }

    fun change(title: String, width: Int, height: Int, resizable: Boolean) {

        MemoryStack.stackPush().use { stack ->
            val titleEncoded = stack.UTF8(title)

            glfwSetWindowTitle(handle, titleEncoded)
            glfwSetWindowSize(handle, width, height)
        }

        _width = width
        _height = height
        // Center the window
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        glfwSetWindowPos(handle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2)

        glfwSetWindowAttrib(handle, GLFW_RESIZABLE, if (resizable) GLFW_TRUE else GLFW_FALSE)
    }

    fun wholeViewport() {
        GL11.glViewport(0, 0, width, height);
    }

    fun enableVSync(interval: Int = 1) {
        glfwSwapInterval(interval)
    }

    fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    private val mouseVector = Vector2d()

    private val xBuffer = BufferUtils.createDoubleBuffer(1)
    private val yBuffer = BufferUtils.createDoubleBuffer(1)

    /**
     * Returns the position of the mouse pointer relative to the top left of the window.
     * This is very rarely useful, and instead, you should use [StageView].mousePosition]
     */
    fun mousePosition(): Vector2d {
        glfwGetCursorPos(handle, xBuffer, yBuffer)

        mouseVector.x = xBuffer.get(0)
        mouseVector.y = yBuffer.get(0)

        return mouseVector
    }

    fun swap() {
        glfwSwapBuffers(handle)
    }

    fun delete() {
        Callbacks.glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
    }

    companion object {
        var current: Window? = null
    }
}

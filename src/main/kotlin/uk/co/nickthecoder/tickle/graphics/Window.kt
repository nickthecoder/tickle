package uk.co.nickthecoder.tickle.graphics

import org.joml.Vector2d
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.stage.View

class Window(
        title: String,
        width: Int,
        height: Int,
        resizable: Boolean = false,
        fullScreen: Boolean = false) {

    val handle: Long

    private var _width = width

    private var _height = height

    val width: Int
        get() = _width

    val height: Int
        get() = _height

    val listeners = mutableListOf<WindowListener>()

    init {

        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

        val monitor = if (fullScreen) glfwGetPrimaryMonitor() else MemoryUtil.NULL
        handle = glfwCreateWindow(_width, _height, title, monitor, MemoryUtil.NULL)
        if (handle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }
        glfwMakeContextCurrent(handle)
        glfwSetWindowAttrib(handle, GLFW_RESIZABLE, if (resizable) GLFW_TRUE else GLFW_FALSE)
        GL.createCapabilities()

        glfwSetKeyCallback(handle) { _, keyCode, scanCode, action, mods ->
            val event = KeyEvent(this, Key.forCode(keyCode), scanCode, ButtonState.of(action), mods)
            listeners.forEach {
                it.onKey(event)
            }
        }

        glfwSetWindowSizeCallback(handle) { _, newWidth, newHeight ->
            if (newWidth != _width || newHeight != _height) {
                val event = ResizeEvent(this, _width, _height, newWidth, newHeight)
                _width = newWidth
                _height = newHeight
                listeners.forEach {
                    it.onResize(event)
                }
            }
        }

        glfwSetMouseButtonCallback(handle) { _, button, action, mods ->
            val event = MouseEvent(this, button, ButtonState.of(action), mods)
            mousePosition(event.screenPosition)
            listeners.forEach {
                it.onMouseButton(event)
            }
        }

    }

    fun showMouse(value: Boolean = true) {
        glfwSetInputMode(handle, GLFW_CURSOR, if (value) GLFW_CURSOR_NORMAL else GLFW_CURSOR_HIDDEN)
    }

    fun close() {
        if (instance === this) {
            instance = null
        }
        glfwSetWindowShouldClose(handle, true)
    }

    fun show() {
        instance = this

        glfwSetWindowShouldClose(handle, false)

        center()

        // Make the OpenGL context instance
        glfwMakeContextCurrent(handle)

        // Make the window visible
        glfwShowWindow(handle)

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

    fun resize(width: Int, height: Int) {
        glfwSetWindowSize(handle, width, height)
        _width = width
        _height = height
    }

    // Center the window
    fun center() {
        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        glfwSetWindowPos(
                handle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        )
    }

    fun wholeViewport() {
        GL11.glViewport(0, 0, width, height);
    }

    fun enableVSync(interval: Int = 1) {
        glfwSwapInterval(interval)
    }

    fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    private val xBuffer = BufferUtils.createDoubleBuffer(1)
    private val yBuffer = BufferUtils.createDoubleBuffer(1)

    /**
     * Returns the position of the mouse pointer relative to the top left of the window.
     * The result is returned by changing the [result] Vector2d (which avoids creating a new object).
     *
     * Often it is easier, and more useful to find the position of the mouse pointer in a view's coordinate system
     * using [View.mousePosition].
     */
    fun mousePosition(result: Vector2d) {
        glfwGetCursorPos(handle, xBuffer, yBuffer)

        result.x = xBuffer.get(0)
        result.y = yBuffer.get(0)
    }

    fun swap() {
        glfwSwapBuffers(handle)
    }

    fun delete() {
        Callbacks.glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
    }

    companion object {
        var instance: Window? = null
    }
}

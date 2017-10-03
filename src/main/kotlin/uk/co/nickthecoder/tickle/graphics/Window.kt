package uk.co.nickthecoder.tickle.graphics

import org.joml.Vector2f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWWindowSizeCallback
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

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, if (resizable) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)

        handle = GLFW.glfwCreateWindow(_width, _height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (handle == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }
    }


    fun keyboardEvents(keyHandler: (KeyEvent) -> Unit) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(handle) { _, keyCode, scanCode, action, mods ->
            keyHandler(KeyEvent(this, Key.forCode(keyCode), scanCode, ButtonState.of(action), mods))
        }
    }

    fun close() {
        if (current === this) {
            current = null
        }
        GLFW.glfwSetWindowShouldClose(handle, true)
    }

    fun show() {
        current = this

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

        // Make the OpenGL context instance
        GLFW.glfwMakeContextCurrent(handle)

        // Make the window visible
        GLFW.glfwShowWindow(handle)

        val resizeCallback = object : GLFWWindowSizeCallback() {
            override fun invoke(window: Long, newWidth: Int, newHeight: Int) {
                _width = newWidth
                _height = newHeight
            }
        }
        GLFW.glfwSetWindowSizeCallback(handle, resizeCallback)
    }


    fun change(title: String, width: Int, height: Int, resizable: Boolean) {

        MemoryStack.stackPush().use { stack ->
            val titleEncoded = stack.UTF8(title)

            GLFW.glfwSetWindowTitle(handle, titleEncoded)
            GLFW.glfwSetWindowSize(handle, width, height)
        }

        _width = width
        _height = height
        // Center the window
        val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
        GLFW.glfwSetWindowPos(handle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2)

        GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_RESIZABLE, if (resizable) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
    }

    fun wholeViewport() {
        GL11.glViewport(0, 0, width, height);
    }

    fun enableVSync(interval: Int = 1) {
        GLFW.glfwSwapInterval(interval)
    }

    fun shouldClose(): Boolean = GLFW.glfwWindowShouldClose(handle)

    private val mouseVector = Vector2f()

    private val xBuffer = BufferUtils.createDoubleBuffer(1)
    private val yBuffer = BufferUtils.createDoubleBuffer(1)

    /**
     * Returns the position of the mouse pointer relative to the top left of the window.
     * This is very rarely useful, and instead, you should use [StageView].mousePosition]
     */
    fun mousePosition(): Vector2f {
        GLFW.glfwGetCursorPos(handle, xBuffer, yBuffer)

        mouseVector.x = xBuffer.get(0).toFloat()
        mouseVector.y = yBuffer.get(0).toFloat()

        return mouseVector
    }

    fun swap() {
        GLFW.glfwSwapBuffers(handle)
    }

    fun delete() {
        Callbacks.glfwFreeCallbacks(handle)
        GLFW.glfwDestroyWindow(handle)
    }

    companion object {
        var current: Window? = null
    }
}

package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.graphics.Window
import java.io.File

abstract class Game(val gameInfo: GameInfo) {

    lateinit var window: Window

    lateinit var gameLoop: GameLoop

    open fun preInitialise() {}

    fun initialise() {
        preInitialise()

        gameLoop = FullSpeedGameLoop(this)

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!GLFW.glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        // Configure GLFW
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable

        // Create the window
        window = Window(gameInfo.title, gameInfo.width, gameInfo.height)

        window.show()

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        postInitialise()
        gameLoop.resetStats()
    }

    open fun postInitialise() {}

    fun loop() {
        while (isRunning()) {
            gameLoop.tick()

            // Poll for window events.
            GLFW.glfwPollEvents()
        }
    }

    open fun isRunning() = !window.shouldClose()

    abstract fun tick()

    fun run() {
        initialise()
        loop()
        cleanUp()
    }

    open fun preCleanup() {
    }

    fun cleanUp() {
        preCleanup()

        // Free the window callbacks and destroy the window
        window.cleanUp()

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null).free()

        postCleanup()
    }

    open fun postCleanup() {
    }

    companion object {

        val resourceDirectory: File by lazy {
            val srcDist = File(File("src"), "dist")
            if (srcDist.exists()) {
                File(srcDist, "resources")
            } else {
                File("resources")
            }
        }

    }

}


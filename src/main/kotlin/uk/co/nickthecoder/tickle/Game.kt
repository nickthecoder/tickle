package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.demo.Director
import uk.co.nickthecoder.tickle.demo.NoDirector
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.loop.FullSpeedGameLoop
import uk.co.nickthecoder.tickle.loop.GameLoop
import java.io.File

abstract class Game(
        val window: Window,
        val resources: Resources) {

    var renderer = Renderer(window)

    var director: Director = NoDirector()

    lateinit var gameLoop: GameLoop

    open fun preInitialise() {}

    fun initialise() {
        preInitialise()

        gameLoop = FullSpeedGameLoop(this)

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

    open fun onKeyEvent(event: KeyEvent) {
        director.onKeyEvent(event)
    }

    fun run() {
        Resources.instance = resources
        initialise()
        loop()
        cleanUp()
    }


    open fun preCleanup() {
    }

    fun cleanUp() {
        preCleanup()

        renderer.delete()
        window.delete()

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

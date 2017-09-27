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

    /**
     * A measure of time in seconds. Updated once per frame, It is actually just System.nano converted to
     * seconds (as a float).
     */
    var seconds: Float = 0f

    /**
     * The time between two "ticks" in seconds.
     */
    var tickDuration: Float = 1f / 60f


    open fun preInitialise() {}

    fun initialise() {
        Game.instance = this

        preInitialise()

        gameLoop = FullSpeedGameLoop(this)

        postInitialise()
        gameLoop.resetStats()

    }

    open fun postInitialise() {}

    fun loop() {
        seconds = System.nanoTime() / 1_000_000_000f
        while (isRunning()) {
            gameLoop.tick()

            GLFW.glfwPollEvents()

            val now = System.nanoTime() / 1_000_000_000f
            tickDuration = now - seconds
            seconds = now
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

        lateinit var instance: Game

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

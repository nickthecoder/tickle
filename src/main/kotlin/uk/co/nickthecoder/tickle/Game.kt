package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.demo.Director
import uk.co.nickthecoder.tickle.demo.NoDirector
import uk.co.nickthecoder.tickle.demo.NoProducer
import uk.co.nickthecoder.tickle.demo.Producer
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.loop.FullSpeedGameLoop
import uk.co.nickthecoder.tickle.loop.GameLoop
import java.io.File

class Game(
        val window: Window,
        val resources: Resources) {

    var renderer = Renderer(window)

    var producer: Producer = NoProducer()
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


    init {
        Resources.instance = resources
        instance = this
        // TODO Move later in the Game lifecycle when scene loading is implemented.
        // At the moment this needs to be here, because Demo creates the actors in it's constructor.

        seconds = System.nanoTime() / 1_000_000_000f
        println("Set initial start time to $seconds")
    }


    fun run() {
        initialise()
        loop()
        cleanUp()
    }

    fun initialise() {
        Game.instance = this

        window.keyboardEvents { onKeyEvent(it) }
        gameLoop = FullSpeedGameLoop(this)

        producer.begin()
        gameLoop.resetStats()

    }

    fun loop() {
        while (isRunning()) {
            gameLoop.tick()

            GLFW.glfwPollEvents()

            val now = System.nanoTime() / 1_000_000_000f
            tickDuration = now - seconds
            seconds = now
        }
    }

    fun cleanUp() {
        renderer.delete()
        window.delete()

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null).free()

        producer.end()
    }

    fun isRunning() = !window.shouldClose()

    fun tick() {

        producer.preTick()
        director.preTick()



        director.postTick()
        producer.postTick()

    }

    fun onKeyEvent(event: KeyEvent) {
        producer.onKeyEvent(event)
        director.onKeyEvent(event)
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

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
import uk.co.nickthecoder.tickle.util.JsonScene
import java.io.File

class Game(
        val window: Window,
        val resources: Resources) {

    var renderer = Renderer(window)

    var producer: Producer = NoProducer()
    var director: Director = NoDirector()
    var scene: Scene = Scene()

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
        producer = resources.gameInfo.createProducer()

        instance = this
        // TODO Move later in the Game lifecycle when scene loading is implemented.
        // At the moment this needs to be here, because Demo creates the actors in it's constructor.

        seconds = System.nanoTime() / 1_000_000_000f
    }

    fun run(sceneFile: File) {
        initialise()
        producer.begin()
        producer.startScene(sceneFile)
        loop()
        cleanUp()
    }

    fun initialise() {
        Game.instance = this

        window.keyboardEvents { onKeyEvent(it) }
        gameLoop = FullSpeedGameLoop(this)

        gameLoop.resetStats()
    }

    fun loop() {
        while (isRunning()) {
            gameLoop.tick()

            GLFW.glfwPollEvents()

            val now = System.nanoTime() / 1_000_000_000f
            tickCount++
            tickDuration = now - seconds
            seconds = now
        }
    }

    fun loadScene(sceneFile: File): SceneResource {
        return JsonScene(sceneFile).sceneResource
    }

    fun startScene(sceneResource: SceneResource) {
        director = Director.createDirector(sceneResource.directorString)
        scene = sceneResource.createScene()

        director.begin()
        scene.begin()
        scene.activated()
        director.activated()
    }

    fun endScene() {
        scene.end()
        director.end()
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

        director.tick()

        director.postTick()
        producer.postTick()

        scene.draw(renderer)
    }

    fun onKeyEvent(event: KeyEvent) {
        producer.onKeyEvent(event)
        director.onKeyEvent(event)
    }

    companion object {

        lateinit var instance: Game

        /**
         * Increments by one for each frame.
         */
        var tickCount: Int = 0

    }

}

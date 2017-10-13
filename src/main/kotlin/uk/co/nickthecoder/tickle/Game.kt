package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
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
    var seconds: Double = 0.0

    /**
     * The time between two "ticks" in seconds.
     */
    var tickDuration: Double = 1.0 / 60.0


    init {
        Resources.instance = resources
        producer = resources.gameInfo.createProducer()

        instance = this
        // TODO Move later in the Game lifecycle when scene loading is implemented.
        // At the moment this needs to be here, because Demo creates the actors in it's constructor.

        seconds = System.nanoTime() / 1_000_000_000.0
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

            val now = System.nanoTime() / 1_000_000_000.0
            tickCount++
            tickDuration = now - seconds
            seconds = now
        }
    }

    fun loadScene(sceneFile: File): SceneResource {
        return JsonScene(sceneFile).sceneResource
    }

    fun startScene(scenePath : String) {
        val file = resources.scenePathToFile(scenePath)
        val sr = loadScene(file)
        startScene(sr)
    }

    fun mergeScene(scenePath : String) {
        val file = resources.scenePathToFile(scenePath)
        val sr = loadScene(file)
        val extraScene = sr.createScene()
        scene.merge(extraScene)
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

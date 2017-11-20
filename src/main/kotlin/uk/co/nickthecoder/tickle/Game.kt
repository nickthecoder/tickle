package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.loop.FullSpeedGameLoop
import uk.co.nickthecoder.tickle.loop.GameLoop
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.SceneResource
import uk.co.nickthecoder.tickle.util.AutoFlushPreferences
import uk.co.nickthecoder.tickle.util.JsonScene
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

class Game(
        val window: Window,
        val resources: Resources) {

    var renderer = Renderer(window)

    var producer: Producer = NoProducer()
    var director: Director = NoDirector()
    var scene: Scene = Scene()

    var gameLoop: GameLoop

    /**
     * A measure of time in seconds. Updated once per frame, It is actually just System.nano converted to
     * seconds.
     */
    var seconds: Double = 0.0

    /**
     * The time between two "ticks" in seconds.
     */
    var tickDuration: Double = 1.0 / 60.0

    private var mouseCapturedBy: MouseButtonHandler? = null

    private val previousScreenMousePosition = Vector2d(-1.0, -1.0)

    private val currentScreenMousePosition = Vector2d()


    val preferences by lazy { AutoFlushPreferences(producer.preferencesRoot()) }

    init {
        Game.instance = this
        Resources.instance = resources
        producer = resources.gameInfo.createProducer()

        instance = this
        seconds = System.nanoTime() / 1_000_000_000.0
        window.keyboardEvents { onKeyEvent(it) }
        window.mouseButtonEvents { onMouseButtonEvent(it) }

        gameLoop = FullSpeedGameLoop(this)
        gameLoop.resetStats()
    }

    fun run(sceneFile: File) {
        producer.begin()
        producer.startScene(sceneFile)
        loop()
        cleanUp()
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

    fun mergeScene(scenePath: String) {
        mergeScene(Resources.instance.scenePathToFile(scenePath))
    }

    fun mergeScene(sceneFile: File) {
        val sr = loadScene(sceneFile)
        val extraScene = sr.createScene()
        scene.merge(extraScene)

        sr.includes.forEach { include ->
            mergeScene(include)
        }
    }

    fun startScene(scenePath: String) {
        startScene(Resources.instance.scenePathToFile(scenePath))
    }

    fun startScene(sceneFile: File) {
        val sr = loadScene(sceneFile)
        startScene(sr)
    }

    private fun startScene(sceneResource: SceneResource) {
        director = Director.createDirector(sceneResource.directorString)
        sceneResource.directorAttributes.applyToObject(director)
        scene = sceneResource.createScene()


        producer.sceneLoaded()
        director.sceneLoaded()

        sceneResource.includes.forEach { include ->
            mergeScene(include)
        }

        producer.sceneBegin()
        director.begin()
        scene.begin()
        scene.activated()
        director.activated()
        producer.sceneActivated()
        System.gc()
    }

    fun endScene() {
        scene.end()
        director.end()
        producer.sceneEnd()
        mouseCapturedBy = null
    }

    fun cleanUp() {
        processRunLater()
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

        processRunLater()

        mouseCapturedBy?.let { capturedBy ->
            if (capturedBy is MouseHandler) {
                Window.instance?.mousePosition(currentScreenMousePosition)
                if (currentScreenMousePosition != previousScreenMousePosition) {
                    previousScreenMousePosition.set(currentScreenMousePosition)
                    val event = MouseEvent(Window.instance!!, 0, ButtonState.UNKNOWN, 0)
                    event.screenPosition.set(currentScreenMousePosition)
                    event.captured = true
                    capturedBy.onMouseMove(event)
                    if (!event.captured) {
                        mouseCapturedBy = null
                    }
                }
            }
        }

        scene.draw(renderer)
    }

    fun onKeyEvent(event: KeyEvent) {
        producer.onKey(event)
        if (event.consumed) {
            return
        }
        director.onKey(event)
    }

    fun onMouseButtonEvent(event: MouseEvent) {
        mouseCapturedBy?.let {
            event.captured = true
            it.onMouseButton(event)
            if (!event.captured) {
                mouseCapturedBy = null
            }
            return
        }

        if (sendMouseButtonEvent(event, producer)) {
            return
        }

        if (sendMouseButtonEvent(event, director)) {
            return
        }

        if (event.state == ButtonState.PRESSED) {
            // TODO Need to iterate BACKWARDS
            scene.views().forEach { view ->
                if (sendMouseButtonEvent(event, view)) {
                    return
                }
            }

        }
    }

    private fun sendMouseButtonEvent(event: MouseEvent, to: MouseButtonHandler): Boolean {
        to.onMouseButton(event)
        if (event.captured) {
            event.captured = false
            mouseCapturedBy = to
            previousScreenMousePosition.set(event.screenPosition)
        }
        return event.consumed
    }

    private var runLaters = ConcurrentLinkedQueue<() -> Unit>()

    private fun processRunLater() {
        var entry = runLaters.poll()
        while (entry != null) {
            entry()
            entry = runLaters.poll()
        }
    }

    fun runLater(func: () -> Unit) {
        runLaters.add(func)
    }

    companion object {

        lateinit var instance: Game

        /**
         * Increments by one for each frame.
         */
        var tickCount: Int = 0

        fun runLater(func: () -> Unit) {
            instance.runLater(func)
        }
    }

}

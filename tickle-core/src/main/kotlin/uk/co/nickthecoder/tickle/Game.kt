/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import org.lwjgl.glfw.GLFW.*
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.loop.FullSpeedGameLoop
import uk.co.nickthecoder.tickle.loop.GameLoop
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.SceneResource
import uk.co.nickthecoder.tickle.util.AutoFlushPreferences
import uk.co.nickthecoder.tickle.util.ErrorHandler
import uk.co.nickthecoder.tickle.util.JsonScene
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

class Game(
        val window: Window,
        val resources: Resources)

    : WindowListener {

    /**
     * When true, prevent all stage, views and roles from receiving tick events.
     *
     * NOTE. [Director] and [Producer] have their tick methods called whether paused or not
     *
     * If you want some role to continue to tick while paused, then add extra code into your
     * [Director]'s or [Producer]'s preTick, tick or postTick methods, manually calling those
     * role's tick method.
     */
    var paused: Boolean = false

    var renderer = Renderer(window)

    var producer: Producer = NoProducer()

    var director: Director = NoDirector()
        set(v) {
            if (v is MouseListener) {
                mouseListeners.remove(v)
            }
            field = v
        }

    var sceneName: String = ""
    var scene: Scene = Scene()

    var gameLoop: GameLoop
    var errorHandler: GameErrorHandler = SimpleGameErrorHandler()

    /**
     * A measure of time in seconds. Updated once per frame, It is actually just System.nano converted to
     * seconds.
     */
    var seconds: Double = 0.0

    /**
     * The time between two "ticks" in seconds.
     */
    var tickDuration: Double = 1.0 / 60.0

    private var mouseCapturedBy: MouseButtonListener? = null

    private val previousScreenMousePosition = Vector2d(-1.0, -1.0)

    private val currentScreenMousePosition = Vector2d()

    private val mouseListeners = mutableListOf<MouseListener>()


    val preferences by lazy { AutoFlushPreferences(producer.preferencesRoot()) }

    init {
        Game.instance = this
        window.enableVSync()
        producer = resources.gameInfo.createProducer()

        instance = this
        seconds = System.nanoTime() / 1_000_000_000.0
        window.listeners.add(this)

        gameLoop = FullSpeedGameLoop(this)
        gameLoop.resetStats()
    }

    fun run(scenePath: String) {
        producer.begin()
        producer.startScene(scenePath)
        loop()
        cleanUp()
    }

    fun loop() {
        while (isRunning()) {
            gameLoop.tick()

            glfwPollEvents()

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
        val oldSceneName = sceneName
        sceneName = scenePath
        try {
            startScene(loadScene(Resources.instance.scenePathToFile(scenePath)))
        } catch (e: Exception) {
            sceneName = oldSceneName
            ErrorHandler.handleError(e)
        }
    }

    private fun startScene(sceneResource: SceneResource) {
        mouseCapturedBy = null

        director = Director.createDirector(sceneResource.directorString)
        sceneResource.directorAttributes.applyToObject(director)
        scene = sceneResource.createScene()


        sceneResource.includes.forEach { include ->
            mergeScene(include)
        }

        producer.sceneLoaded()
        director.sceneLoaded()

        producer.sceneBegin()
        director.begin()
        scene.begin()
        scene.activated()
        director.activated()
        producer.sceneActivated()
        seconds = System.nanoTime() / 1_000_000_000.0
        gameLoop.sceneStarted()
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
        window.listeners.remove(this)
        producer.end()
    }


    var quitting = false

    fun quit() {
        quitting = true
    }

    /**
     * Returns true until the game is about to end. The game can end either by calling [quit], or
     * by closing the window (e.g. Alt+F4, or by clicking the window's close button).
     *
     * In both cases, the game doesn't stop immediately. The game loop is completed, allowing
     * everything to end cleanly.
     *
     * NOTE. isRunning ignores the [paused] boolean. i.e. a paused game is still considered to be running.
     */
    fun isRunning() = !quitting && !window.shouldClose()

    /**
     * Called by the [GameLoop]. Do NOT call this directly from elsewhere!
     *
     * If the mouse position has moved, then create a MouseEvent, and call all mouse listeners.
     *
     * Note, if the mouse has been captured, then only the listener that has captured the mouse
     * will be notified of the event.
     */
    fun mouseMoveTick() {
        Window.instance?.mousePosition(currentScreenMousePosition)

        if (currentScreenMousePosition != previousScreenMousePosition) {
            previousScreenMousePosition.set(currentScreenMousePosition)

            var button = -1
            for (b in 0..2) {
                val state = glfwGetMouseButton(window.handle, b)
                if (state == GLFW_PRESS) {
                    button = b
                }
            }
            val event = MouseEvent(Window.instance!!, button, if (button == -1) ButtonState.UNKNOWN else ButtonState.PRESSED, 0)
            event.screenPosition.set(currentScreenMousePosition)

            mouseCapturedBy?.let { capturedBy ->
                if (capturedBy is MouseListener) {

                    event.captured = true
                    capturedBy.onMouseMove(event)
                    if (!event.captured) {
                        mouseCapturedBy = null
                    }
                    event.consume()
                }
            }
            if (!event.isConsumed()) {
                for (ml in mouseListeners) {
                    ml.onMouseMove(event)
                    if (event.captured) {
                        mouseCapturedBy = ml
                        break
                    }
                    if (event.isConsumed()) break
                }
            }
        }
    }

    override fun onKey(event: KeyEvent) {
        producer.onKey(event)
        if (event.isConsumed()) {
            return
        }
        director.onKey(event)
    }

    fun addMouseListener(listener: MouseListener) {
        mouseListeners.add(listener)
    }

    fun removeMouseListener(listener: MouseListener) {
        mouseListeners.remove(listener)
    }

    override fun onMouseButton(event: MouseEvent) {
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

    override fun onResize(event: ResizeEvent) {
        producer.onResize(event)
    }

    private fun sendMouseButtonEvent(event: MouseEvent, to: MouseButtonListener): Boolean {
        to.onMouseButton(event)
        if (event.captured) {
            event.captured = false
            mouseCapturedBy = to
            previousScreenMousePosition.set(event.screenPosition)
        }
        return event.isConsumed()
    }

    /**
     * Uses a ConcurrentLinkedQueue, rather than a simple list, so that [runLater] can be called within
     * a runLater lambda without fear of a concurrent modification exception.
     */
    private var runLaters = ConcurrentLinkedQueue<() -> Unit>()

    /**
     * Called by the [GameLoop] (do NOT call this directly from elsewhere!)
     *
     * Runs all of the lambdas sent to [runLater] since the last call to [processRunLater].
     */
    fun processRunLater() {
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

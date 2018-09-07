package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Button

open class TestDirector : AbstractDirector() {

    @Attribute
    var nextScene: String = ""

    var testCount: Int = 0


    var okCount = 0

    override fun activated() {
        println("Test Scene ${Game.instance.sceneName} started.")
    }

    open fun stop() {
        Game.instance.scene.stages.values.forEach { stage ->
            stage.actors.forEach { actor ->
                if (actor.role !is Button) {
                    actor.role = null
                }
            }
        }
    }

    open fun passed() {
        okCount++
        println("Test $okCount of $testCount OK")
        if (okCount == testCount) {
            println("Test Scene ${Game.instance.sceneName} passed")
            if (nextScene.isBlank()) {
                stop()
            } else {
                Game.instance.startScene(nextScene)
            }
        }
    }

    fun failed(message: String, expected: Any?, actual: Any?) {
        println("FAIL.Scene:${Game.instance.sceneName}. $message. Expected:$expected. Actual:$actual")
        stop()
    }

    fun doubleEquals(a: Double, b: Double, error: Double) = Math.abs(a - b) < error

    fun assertEquals(message: String, expected: Double, actual: Double, error: Double = 1.001) {
        if (doubleEquals(expected, actual, error)) {
            passed()
        } else {
            failed(message, expected, actual)
        }
    }

    fun assertEquals(message: String, expected: Vector2d, actual: Vector2d, error: Double = 1.0001) {
        if (doubleEquals(expected.x, actual.x, error) &&
                doubleEquals(expected.y, actual.y, error)) {
            passed()
        } else {
            failed(message, expected, actual)
        }
    }

}
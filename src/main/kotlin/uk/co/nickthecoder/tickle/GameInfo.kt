package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import java.io.File

class GameInfo(
        var title: String,
        var width: Int,
        var height: Int,
        var resizable: Boolean,
        var initialScenePath: File = File("splash"),
        var testScenePath: File = File(""),
        var producerString: String = NoProducer::class.java.name,
        var physicsEngine: Boolean = false,
        var gravity: Vector2d = Vector2d(0.0, 0.0),
        var velocityIterations: Int = 8,
        var positionIterations: Int = 3,
        var scale: Double = 100.0) {

    fun createProducer(): Producer {
        try {
            val klass = Class.forName(producerString)
            val newProducer = klass.newInstance()
            if (newProducer is Producer) {
                return newProducer
            } else {
                System.err.println("'$producerString' is not a type of Producer")
            }
        } catch (e: Exception) {
            System.err.println("Failed to create a Producer from : '$producerString'")
        }
        return NoProducer()
    }

}


package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.physics.PhysicsInfo
import java.io.File

class GameInfo(
        var title: String,
        var id: String, /* simple text - no spaces or punctuation, will NOT be translated if I18N is implemented */
        var width: Int,
        var height: Int,
        var fullScreen : Boolean,
        var resizable: Boolean,
        var initialScenePath: File = File("menu"),
        var testScenePath: File = File("menu"),
        var producerString: String = NoProducer::class.java.name,
        var physicsEngine: Boolean = false) {

    val physicsInfo = PhysicsInfo()

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


package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.demo.NoProducer
import uk.co.nickthecoder.tickle.demo.Producer

class GameInfo(
        var title: String,
        var width: Int,
        var height: Int,
        var resizable: Boolean,
        var startScene: String = "menu",
        var producerString: String = NoProducer::class.java.name) {

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


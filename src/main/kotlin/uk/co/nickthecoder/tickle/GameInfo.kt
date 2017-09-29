package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.demo.NoProducer

class GameInfo(
        var title: String,
        var width: Int,
        var height: Int,
        var resizable: Boolean,
        var producerString: String = NoProducer::class.java.name) {
}

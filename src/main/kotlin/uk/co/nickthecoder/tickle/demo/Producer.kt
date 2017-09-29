package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.events.KeyEvent

/**
 * Looks at the big picture, and shouldn't be involved in the minor details.
 * Your Producer class (if you have one) should named after your game. e.g. "SpaceInvaders"
 * Many games don't need a Producer, and therefore use the class NoProducer.
 *
 * Example responsibilities of a Producer :
 *
 * Perform actions when the game first starts and when it ends.
 * Add additional auto-generated resources to the Resources class at the beginning of the game.
 * Take a snapshot of the screen when a particular key is pressed.
 * To keep track of the score, and high-score tables (Old school!)
 * To hold information carried forward from one level to the next, such as lives remaining.
 *
 * See Director
 */
interface Producer {

    fun begin() {}

    fun startScene(sceneName: String) {}

    fun preTick() {}

    fun postTick() {}

    fun end() {}

    fun onKeyEvent(event: KeyEvent) {}
}

abstract class AbstractProducder() : Producer {
    // TODO Load scene
}

class NoProducer() : AbstractProducder() {

}

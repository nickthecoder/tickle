package scripts

import uk.co.nickthecoder.tickle.AbstractRole

class ExampleKotlinRole : AbstractRole() {

    override fun tick() {
        actor.x += 1
    }
}

package uk.co.nickthecoder.tickle.demo.physics

import uk.co.nickthecoder.tickle.AbstractRole

class Ground : AbstractRole() {

    override fun activated() {
        println("Ground : ${actor.body?.jBox2DBody?.position}")
    }

    override fun tick() {
    }
}

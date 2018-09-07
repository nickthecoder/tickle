package uk.co.nickthecoder.tickle.demo.overlapping

import uk.co.nickthecoder.tickle.collision.Overlapping
import uk.co.nickthecoder.tickle.collision.PixelOverlapping
import uk.co.nickthecoder.tickle.demo.TestDirector

open class OverlappingDirector : TestDirector() {

    lateinit var overlapping: Overlapping

    override fun begin() {
        super.begin()
        overlapping = createOverlapping()
    }

    open fun createOverlapping(): Overlapping = PixelOverlapping()
}

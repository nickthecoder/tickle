package uk.co.nickthecoder.tickle.demo.overlapping

import uk.co.nickthecoder.tickle.collision.Overlapping
import uk.co.nickthecoder.tickle.collision.PixelOverlapping

open class ThresholdDirector : OverlappingDirector() {

    override fun createOverlapping() : Overlapping = PixelOverlapping().threshold(128)
}

package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.FixtureDef

class TickleFixtureDef(
        var shapeDef: ShapeDef)

    : FixtureDef() {

    init {
        // The default categoryBits is 1, which seems wrong to me, I think a better default would be
        // ALL of the bits set (the same as the mask).
        filter.categoryBits = filter.maskBits
    }
}

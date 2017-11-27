package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.FixtureDef
import uk.co.nickthecoder.tickle.util.Copyable

class TickleFixtureDef(
        var shapeDef: ShapeDef)

    : FixtureDef(), Copyable<TickleFixtureDef> {

    init {
        // The default categoryBits is 1, which seems wrong to me, I think a better default would be
        // ALL of the bits set (the same as the mask).
        filter.categoryBits = filter.maskBits
    }

    override fun copy(): TickleFixtureDef {
        val copy = TickleFixtureDef(shapeDef.copy())
        copy.isSensor = isSensor
        copy.restitution = restitution
        copy.density = density
        copy.friction = friction

        copy.filter.categoryBits = filter.categoryBits
        copy.filter.maskBits = filter.maskBits
        copy.filter.groupIndex = filter.groupIndex

        return copy
    }
}

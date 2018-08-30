package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.BodyDef
import uk.co.nickthecoder.tickle.util.Copyable

/**
 * Holds details of a Costume's body. This differs from the usual [BodyDef], by defining the FixturesDef
 * rather than raw Shape objects.
 * Also note that the units used here are in Tickle's (unscaled) coordinate system (which is generaly pixels),
 * whereas the Body objects that get created via this TickleBodyDef use JBox2D's coordinate system, which is
 * (apparently), not suitable for units of pixels, but much smaller (the docs say objects must be up to 10 units
 * in size).
 * Therefore, when converting this TickleBodyDef to an actual Body, the [TickleWorld]'s scale is used to convert
 * the units.
 */
class TickleBodyDef : BodyDef(), Copyable<TickleBodyDef> {

    val fixtureDefs = mutableListOf<TickleFixtureDef>()

    override fun copy(): TickleBodyDef {
        val copy = TickleBodyDef()
        copy.bullet = bullet
        copy.fixedRotation = fixedRotation
        copy.linearDamping = linearDamping
        copy.angularDamping = angularDamping
        copy.position.set(position)
        copy.type = type
        copy.allowSleep = allowSleep

        fixtureDefs.forEach { fixtureDef ->
            copy.fixtureDefs.add(fixtureDef.copy())
        }

        return copy
    }
}

/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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

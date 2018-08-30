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

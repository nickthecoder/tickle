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
package uk.co.nickthecoder.tickle.collision

import uk.co.nickthecoder.tickle.Actor

/**
 * Uses [PixelOverlapping], but with a threshold value, so that the fuzzy edges of images do not cause objects
 * to be considered overlapping, when they appear to be a pixel or two apart.
 *
 * We compare the alpha channel of the result of overlapping the two images with the threshold. If any pixel is
 * above the threshold, then the Actors are considered to be touching.
 * A threshold of zero means they are overlapping if any pixels that are event slightly opaque overlap.
 * In practice, 0 is a bad default, but the "perfect" default value isn't obvious!
 */
class ThresholdPixelOverlapping(val threshold: Int, val pixelOverlap: PixelOverlapping)
    : Overlapping {

    override fun overlapping(actorA: Actor, actorB: Actor): Boolean {
        return pixelOverlap.overlapping(actorA, actorB, threshold)
    }

    override fun toString() = "ThresholdPixelOverlapping( size=${pixelOverlap.size}, threshold=$threshold )"
}

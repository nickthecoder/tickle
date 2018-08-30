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
package uk.co.nickthecoder.tickle

/**
 * Common attributes shared by both Actor and ActorResource.
 *
 * Currently, this is only used by StageView, so that Actors and ActorResources can be sorted when rendering, and
 * sorted in the opposite order when checking for Actors/ActorResources at a point.
 *
 * Other common attributes may be moved into here if the need arises.
 */
interface ActorDetails {

    var x: Double
    var y: Double
    var zOrder: Double

}

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
package uk.co.nickthecoder.tickle.util

/**
 * Any classes which implement SimpleInstance can be used as an [Attribute].
 * Instead of choosing a value, you choose which sub-class to instantiate.
 * For example, [ButtonEffects] implements SimpleInstance, and therefore the [Button] role can
 * have a [ButtonEffects] as an attribute. The user can then pick and sub-class of [ButtonEffects]
 * as the value.
 *
 * Note, for this to work, all sub-classes must have a zero-argument constructor.
 */
interface SimpleInstance

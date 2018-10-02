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

interface Deletable {

    /**
     * Returns a list of [Dependable] items, which must be taken care of, before this
     * can be deleted.
     *
     * Some Dependable items must be deleted by the game designer, whereas others can be broken
     * automatically, without user intervention. See [Dependable].
     */
    fun dependables(): List<Dependable>

    fun delete()

}

/**
 * An object which has dependents, such that a dependent cannot be deleted without either :
 * - Also deleting the Dependable
 * - Break the dependency in another way
 *
 * When trying to delete a dependant, if there are only breakable dependencies, then the delete
 * can proceed. Otherwise, the delete is forbidden (until the user manually breaks the dependency,
 * often by deleting the Dependable first).
 *
 * For example, Texture is Dependable (and its dependants are Poses)
 */
interface Dependable {

    /**
     * Can the dependency be broken? (i.e. can we delete this)
     */
    fun isBreakable(dependency: Deletable): Boolean = false

    /**
     * Only called if isBreakable(dependant) == true
     *
     * Examples :
     * - A Deletable Costume will remove actors from a Dependant Scene
     * - A Costume will remove Deletable Poses from its list of events
     */
    fun breakDependency(dependency: Deletable) {}
}

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

import uk.co.nickthecoder.tickle.Role

/**
 * Tag your roles, so that they can later be found from other Roles.
 * For example, you could use a "deadly" tag, and if an actor collides with any roles with that tag,
 * then one (or both) of the colliding actors die!
 *
 * It is also common to find Tagged Roles from with a Director. For example, at the start of a Scene,
 * a Director may count all roles tagged "collectable".
 * Each time an item is collected, decrement the count. The scene is complete when the
 * counter reaches zero.
 *
 * IMPORTANT : Any roles which have a Tagged object MUST clear it when the actor dies.
 * (Place : tagged.clear() in the Role's end() method).
 * Otherwise you will end up interacting with dead roles.
 * This is a common error which can lead to collisions with invisible objects!
 *
 * Failing to clear Tagged roles from dead Actors can also harm performance.
 *
 * Note that tags can be Any objects; two good choices are String or an enum class that you create
 * specifically to act as tags.
 *
 * I recommend using an enum, because then the compiler can detect typos.
 */
class Tagged(
        val tagManager: TagManager,
        val role: TaggedRole) {

    private val tags = mutableSetOf<Any>()

    fun findRoles(tag: Any) = tagManager.findRoles(tag)

    fun findARole(tag: Role) = tagManager.findARole(tag)

    fun closest(tag: Role) = tagManager.closest(role, tag)

    fun add(vararg tag: Any) {
        tag.forEach { tags.add(it) }
        tagManager.add(role, * tag)
    }

    fun remove(vararg tag: Any) {
        tag.forEach { tags.remove(it) }
        tagManager.add(role, * tag)
    }

    fun clear() {
        tags.forEach { tagManager.remove(role, it) }
        tags.clear()
    }

}

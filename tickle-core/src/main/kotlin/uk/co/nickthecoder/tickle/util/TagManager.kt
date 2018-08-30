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
 * Remembers which Roles have which tags. Director has a TagManager, and is the easiest way to manage tags.
 * However, if you need multiple TagManager per Scene, then you do not need to use the one on Director.
 */
class TagManager {

    private val tagRoleMap = mutableMapOf<Any, MutableSet<TaggedRole>>()

    fun findRoles(tag: Any): Set<TaggedRole> = tagRoleMap[tag] ?: emptySet()

    fun findARole(tag: Any): Role? = tagRoleMap[tag]?.firstOrNull()


    /**
     * Returns the closest role with the given tag, or null if there are no roles with that tag.
     * 'toMe' is never returned.
     */
    fun closest(toMe: Role, tag: Any): Role? {
        return toMe.closest(findRoles(tag))
    }

    internal fun add(role: TaggedRole, vararg tags: Any) {
        tags.forEach { tag ->
            val set = tagRoleMap[tag]
            if (set == null) {
                val newSet = mutableSetOf(role)
                tagRoleMap[tag] = newSet
            } else {
                set.add(role)
            }
        }
    }

    internal fun remove(role: Role, vararg tags: Any) {
        tags.forEach { tag ->
            tagRoleMap[tag]?.remove(role)
        }
    }

}

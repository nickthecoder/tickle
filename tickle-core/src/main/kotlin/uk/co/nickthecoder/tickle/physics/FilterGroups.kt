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

interface FilterGroups {
    fun values(): Map<String, Int>
}

class NoFilterGroups : FilterGroups {
    override fun values(): Map<String, Int> = mapOf("None" to 0)
}

class ExampleFilterGroups : FilterGroups {
    override fun values(): Map<String, Int> {
        return ExampleFilterGroup.values().associateBy({ it.name }, { it.index })
    }
}

enum class ExampleFilterGroup(val index: Int) {
    None(0), Land(1), Sea(2), Air(3)
}

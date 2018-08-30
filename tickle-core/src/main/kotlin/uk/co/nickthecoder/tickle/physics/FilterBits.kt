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

interface FilterBits {
    fun values(): Map<String, Int>

    fun columns(): Int = 4
}

class NoFilterBits : FilterBits {
    override fun values(): Map<String, Int> = emptyMap()
}

class ExampleFilterBits : FilterBits {
    override fun values(): Map<String, Int> {
        return ExampleFilterBit.values().associateBy({ it.name }, { it.bit })
    }
}

enum class ExampleFilterBit(val bit: Int) {
    A(1), B(2), C(4), D(8);
}

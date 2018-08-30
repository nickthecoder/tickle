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
package uk.co.nickthecoder.tickle.graphics

enum class Usage(val value: Int) {
    STREAM_DRAW(0x88E0),
    STREAM_READ(0x88E1),
    STREAM_COPY(0x88E2),
    STATIC_DRAW(0x88E4),
    STATIC_READ(0x88E5),
    STATIC_COPY(0x88E6),
    DYNAMIC_DRAW(0x88E8),
    DYNAMIC_READ(0x88E9),
    DYNAMIC_COPY(0x88EA)
}
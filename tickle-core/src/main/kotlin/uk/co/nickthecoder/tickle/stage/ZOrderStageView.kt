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
package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.ActorDetails

class ZOrderComparator : Comparator<ActorDetails> {
    override fun compare(o1: ActorDetails, o2: ActorDetails): Int = Math.signum(o1.zOrder - o2.zOrder).toInt()
}

class ZOrderStageView
    : AbstractStageView(ZOrderComparator()) {
}

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
package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource

interface HasTask {

    fun task(): Task

}

interface SnapTo : HasTask {

    fun snapActor(actorResource: DesignActorResource, adjustments: MutableList<Adjustment>)

    fun snapInfo() = "You can temporarily disable snapping by holding down the ctrl key while dragging."
}

data class Adjustment(var x: Double = 0.0, var y: Double = 0.0, var score: Double = Double.MAX_VALUE) {
    fun reset() {
        x = 0.0
        y = 0.0
        score = Double.MAX_VALUE
    }
}

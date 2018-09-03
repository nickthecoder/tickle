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
package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Do
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Button

class Info : Button() {

    val action = Delay(1.0)
            .then(Do { actor.textAppearance?.text = text() })
            .forever()

    override fun activated() {
        super.activated()
        actor.color = Color.white().semi()
        action.begin()
    }

    override fun tick() {
        action.act()
    }

    override fun onClicked(event: MouseEvent) {
        Game.instance.gameLoop.resetStats()
        actor.textAppearance?.text = text()
    }

    override fun stateChanged(down: Boolean) {
        actor.color = if (down) Color.white() else Color.white().semi()
    }

    fun text(): String {
        val fps = Math.round(Game.instance.gameLoop.actualFPS())
        val stageInfo = Game.instance.scene.stages.map { (name, stage) ->
            "$name has ${stage.actors.count()} actors"
        }.joinToString(separator = "\n")
        return "$stageInfo\n$fps FPS"
    }
}

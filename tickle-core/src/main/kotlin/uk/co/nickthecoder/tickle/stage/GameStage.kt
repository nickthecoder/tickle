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

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.physics.TickleWorld


/**
 * The standard implementation of [Stage].
 */
open class GameStage() : Stage {

    protected val mutableViews = mutableListOf<StageView>()

    override val views: List<StageView> = mutableViews

    override var world: TickleWorld? = null

    protected val mutableActors = mutableSetOf<Actor>()

    override val actors: Set<Actor> = mutableActors

    override fun begin() {
        // Note. We create a new list (of Role), so that there is no concurrent modification exception if an actor is
        // removed from the stage.
        actors.map { it.role }.forEach { role ->
            if (role?.actor?.stage != null) {
                role.begin()
            }
        }
    }

    override fun activated() {
        // Note. We create a new list (of Role), so that there is no concurrent modification exception if an actor is
        // removed from the stage.
        actors.map { it.role }.forEach { role ->
            if (role?.actor?.stage != null) {
                role.activated()
            }
        }
    }

    override fun end() {
        // Note. We create a new list (of Role), so that there is no concurrent modification exception if an actor is
        // removed from the stage.
        actors.map { it.role }.forEach { role ->
            if (role?.actor?.stage != null) {
                role.end()
            }
        }
        mutableActors.clear()
    }

    override fun tick() {
        // Note. We create a new list (of Role), so that there is no concurrent modification exception if an actor is
        // removed from the stage.
        actors.map { it.role }.forEach { role ->
            if (role?.actor?.stage != null) {
                role.tick()
            }
        }
    }

    override fun add(actor: Actor, activate: Boolean) {

        actor.costume.bodyDef?.let { bodyDef ->
            world?.createBody(bodyDef, actor)
        }

        mutableActors.add(actor)
        actor.stage = this
        actor.role?.begin()
        if (activate) {
            actor.role?.activated()
        }
    }

    override fun remove(actor: Actor) {
        actor.body?.let {
            world?.destroyBody(it)
            actor.body = null
        }
        mutableActors.remove(actor)
        actor.role?.end()
    }

    override fun addView(view: StageView) {
        mutableViews.add(view)
    }

}

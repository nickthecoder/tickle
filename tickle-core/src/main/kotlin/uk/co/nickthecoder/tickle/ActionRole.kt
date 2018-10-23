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
package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.ActionHolder
import uk.co.nickthecoder.tickle.action.Kill

/**
 * A Role that only has a single Action.
 * It is a convenient way of using an Action within a Role.
 *
 * The hard work is done by the super class [ActionHolder].
 *
 * Instead of overriding the tick method, an ActionRole override [createAction].
 * It can override tick as well if it need to, but be sure to call super.tick()!
 *
 * Note, if you want the Actor to die, then ensure you include a [Kill] action,
 * otherwise the Actor will stay living, but do nothing.
 */
open class ActionRole(private val defaultAction: Action?) : ActionHolder(), Role {

    constructor() : this(null)

    override lateinit var actor: Actor

    override fun begin() {}

    override fun activated() {
        then(createAction())
    }

    open fun createAction(): Action? = defaultAction

    override fun tick() {
        act()
    }

    override fun end() {}

}

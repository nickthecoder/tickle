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

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction
import uk.co.nickthecoder.tickle.util.Tagged
import uk.co.nickthecoder.tickle.util.TaggedRole

abstract class Controllable : AbstractRole(), TaggedRole {

    var hasInput: Boolean = false

    override lateinit var tagged: Tagged

    var movement: Action = NoAction()

    override fun begin() {
        tagged = Tagged(Play.instance.tagManager, this)
        tagged.add(DemoTags.CONTROLLABLE)
    }

    override fun activated() {
        movement.begin()
    }

    override fun tick() {
        if (hasInput) {
            movement.act()
        }
    }

    override fun end() {
        tagged.clear()
    }
}

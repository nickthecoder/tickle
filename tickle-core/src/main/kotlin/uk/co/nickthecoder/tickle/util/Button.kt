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

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseListener

/**
 * You can use one of the built-in buttons, such as [QuitButton] and [SceneButton], or you can write your own.
 * Create a sub-class of Button, and write an [onClicked] method.
 *
 * You can add "special effects" to a button, such as enlarging the button while the mouse hovers over it,
 * changing its color. In fact you can do whatever you like.
 * From within the Tickle resources editor, edit your button's Costume, and you should see an item labelled
 * "effects" in the "Attributes" section.
 *
 * You can choose one of the built-in classes ([ExampleButtonEffects] and [EventButtonEffects]), or create
 * your own. See [ButtonEffects] for more information.
 */
abstract class Button : ActionRole(), MouseListener {

    @CostumeAttribute
    var effects: ButtonEffects? = null

    var enableEnterExit: Boolean = true

    var enabled: Boolean = true
        set(v) {
            if (field != v) {
                field = v
                then(if (v) effects?.enable(this) else effects?.disable(this))
            }
        }

    private var down: Boolean = false
        set(v) {
            if (v != field) {
                field = v
                then(if (v) effects?.down(this) else effects?.up(this))
            }
        }

    private val mousePosition = Vector2d()

    private var mouseHovering: Boolean = false
        set(v) {
            if (v != field) {
                field = v
                then(if (v) effects?.enter(this) else effects?.exit(this))
            }
        }

    override fun activated() {
        if (effects?.enter(this) == null && effects?.exit(this) == null) {
            enableEnterExit = false
        }
    }

    override fun tick() {
        super.tick()

        if (enableEnterExit) {
            actor.stage?.firstView()?.let {
                it.mousePosition(mousePosition)
                mouseHovering = actor.contains(mousePosition)
            }
        }
    }

    override fun onMouseButton(event: MouseEvent) {
        if (enabled) {
            if (event.state == ButtonState.PRESSED) {
                event.capture()
                down = true
            } else if (event.state == ButtonState.RELEASED) {
                event.release()
                if (down) {
                    val action = effects?.clicked(this)
                    action?.let { then(it) }
                    then { onClicked(event) }
                }
                down = false
            }
        }
    }

    override fun onMouseMove(event: MouseEvent) {
        down = actor.touching(event.viewPosition)
    }

    abstract fun onClicked(event: MouseEvent)

}

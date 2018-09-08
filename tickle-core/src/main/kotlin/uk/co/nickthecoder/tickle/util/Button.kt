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
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Do
import uk.co.nickthecoder.tickle.action.SequentialAction
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseListener

abstract class Button : AbstractRole(), MouseListener {

    @CostumeAttribute
    var actions: ButtonActions? = null

    var enableEnterExit: Boolean = true

    var enabled: Boolean = true
        set(v) {
            field = v
            addAction(if (v) actions?.enableAction(this) else actions?.disableAction(this))
        }

    private var currentAction: SequentialAction? = null
        set(v) {
            if (v?.begin() == true) {
                field = null
            } else {
                field = v
            }
        }

    private var down: Boolean = false
        set(v) {
            if (v != field) {
                field = v
                addAction(if (v) {
                    actions?.downAction(this)
                } else {
                    actions?.upAction(this)
                })
            }
        }

    private val mousePosition = Vector2d()

    private var mouseHovering: Boolean = false
        set(v) {
            if (v != field) {
                field = v
                addAction(if (v) {
                    actions?.enterAction(this)
                } else {
                    actions?.exitAction(this)
                })
            }
        }

    override fun activated() {
        if (actions?.enterAction(this) == null && actions?.exitAction(this) == null) {
            enableEnterExit = false
        }
    }

    override fun tick() {
        currentAction?.let {
            if (it.act()) {
                currentAction = null
            }
        }
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
                    val action = actions?.clickedAction(this)
                    addAction(action)
                    addAction(Do { onClicked(event) })
                }
                down = false
            }
        }
    }

    override fun onMouseMove(event: MouseEvent) {
        down = actor.touching(event.viewPosition)
    }

    abstract fun onClicked(event: MouseEvent)

    private fun addAction(action: Action?) {
        if (action == null) return

        if (currentAction == null) {
            currentAction = SequentialAction(action)
        } else {
            currentAction?.add(action)
        }
    }
}

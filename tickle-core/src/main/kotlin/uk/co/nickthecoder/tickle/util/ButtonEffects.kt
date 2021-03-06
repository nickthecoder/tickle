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

import uk.co.nickthecoder.tickle.action.Action

/**
 * Adds special effects to a Button.
 * By having the special effects in a separate class hierarchy to Button, we can mix and match them.
 * For example, a QuitButton ans a SceneButton can both share common ButtonEffects.
 *
 * To create your own effects, create a new class, which implements [ButtonEffects], and then
 * it should appear in the "Attributes" section of you button's Costume within the Tickle resources editor.
 *
 * Note, you must have a default constructor (i.e. a constructor which takes no arguments).
 */
interface ButtonEffects : SimpleInstance {

    /**
     * The Action to perform when the button is held down.
     * The action should be QUICK, because if the mouse is moved out quickly, then the
     * button will seem laggy.
     */
    fun down(button: Button): Action? = null

    /**
     * The Action to perform when the mouse moves away from the button before being released.
     */
    fun up(button: Button): Action? = null

    /**
     * Whenever the mouse enters the Button area (also known as "hover")
     */
    fun enter(button: Button): Action? = null

    /**
     * Whenever the mouse exists the Button's area (i.e. when it stops hovering)
     */
    fun exit(button: Button): Action? = null

    /**
     * The Action to perform when the button is clicked.
     * Note, the Button's onClicked() method will be called AFTER this Action has finished.
     */
    fun clicked(button: Button): Action? = null

    /**
     * When the button is disabled.
     * A typical action is to make the button grey, or semi-transparent, to indicate that it is not enabled.
     */
    fun disable(button: Button): Action? = null

    /**
     * When the button is re-enabled.
     * Typically this returns the button to its former glory, after being greyed out, or made transparent by [disable].
     */
    fun enable(button: Button): Action? = null


}

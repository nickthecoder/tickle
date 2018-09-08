package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.action.Action

interface ButtonActions : SimpleInstance {

    /**
     * The Action to perform when the button is held down.
     * The action should be QUICK, because if the mouse is moved out quickly, then the
     * button will seem laggy.
     */
    fun downAction(button: Button): Action? = null

    /**
     * The Action to perform when the mouse moves away from the button before being released.
     */
    fun upAction(button: Button): Action? = null

    /**
     * The Action to perform when the button is clicked.
     * Note, the Button's onClicked() method will be called AFTER this Action has finished.
     * If null is returned, then the onClicked() will happen immediately.
     */
    fun clickedAction(button: Button): Action? = null

    fun enableAction(button: Button): Action? = null

    fun disableAction(button: Button): Action? = null

}

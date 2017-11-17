package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.events.MouseButtonHandler
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.util.Recti

interface View : MouseButtonHandler {

    var zOrder: Int

    var rect: Recti

    var handleMouseButtons: Boolean

    fun tick()

    fun draw(renderer: Renderer)

    /**
     * Convert the x and y values in screen units (where 0,0 is the top left edge of the screen) to the view's x,y coordinates.
     * Note that the screen's y axis points DOWN while the view's y axis points UP.
     * The result is returned by changing the [into] Vector2d (and therefore avoiding creating a new Vector2d object).
     *
     * Note, [screen] and [into] may be the SAME instance
     */
    fun screenToView(screen: Vector2d, into: Vector2d)

    /**
     * Convert the x and y values in screen units (where 0,0 is the top left edge of the screen) to the view's x,y coordinates.
     * Note that the screen's y axis points DOWN while the view's y axis points UP.
     * The result is returned in a NEW Vector2d object.
     */
    fun screenToView(screen: Vector2d): Vector2d {
        val result = Vector2d()
        screenToView(screen, result)
        return result
    }

    /**
     * Finds the position of the mouse pointer in the view's coordinate system.
     */
    fun mousePosition(result: Vector2d) {
        Window.instance?.let {
            it.mousePosition(result)
            screenToView(result, result)
            return
        }
        result.set(0.0, 0.0)
    }

}

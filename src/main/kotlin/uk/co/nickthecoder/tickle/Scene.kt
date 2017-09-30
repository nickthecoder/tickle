package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.stage.AutoPosition
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.stage.View

class Scene {

    var background: Color = Color.BLACK

    val stages = mutableMapOf<String, Stage>()

    private val views = mutableMapOf<String, View>()

    private val autoPositions = mutableMapOf<String, AutoPosition>()

    fun layout(width: Int, height: Int) {
        autoPositions.forEach { name, position ->
            views[name]?.let { view ->
                view.rect = position.rect(width, height)
            }
        }
    }

    fun addView(name: String, view: View, position: AutoPosition? = null) {
        views[name] = view
        position?.let {
            autoPositions[name] = it
        }
    }

    fun removeView(name: String) {
        views.remove(name)
        autoPositions.remove(name)
    }

    fun draw(renderer: Renderer) {
        layout(renderer.window.width, renderer.window.height)

        // TODO How should these be ordered?
        views.values.forEach { view ->
            view.draw(renderer)
        }
    }
}

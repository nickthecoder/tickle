package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.stage.View

class Scene {

    var background: Color = Color.BLACK

    private val stages = mutableMapOf<String, Stage>()

    private val views = mutableMapOf<String, View>()

    fun draw(renderer: Renderer) {
        // TODO How should these be ordered?
        views.values.forEach { view ->
            view.draw(renderer)
        }
    }
}

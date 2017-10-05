package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.SceneResource

/**
 */
class Layers(sceneResource: SceneResource) {

    val stack = StackPane()

    private val allLayers = mutableListOf<Layer>()

    private val stageLayers = mutableListOf<StageLayer>()

    val glass = GlassLayer()

    init {

        sceneResource.sceneStages.forEach { stageName, sceneStage ->
            val layer = StageLayer(sceneResource, stageName, sceneStage)
            add(layer)
            stageLayers.add(layer)
        }
        add(glass)
    }

    fun add(layer: Layer) {
        allLayers.add(layer)
        stack.children.add(layer.canvas)
    }

    fun eventToWorldX(event: MouseEvent): Float {
        // TODO Update this when panning is implemented
        return event.x.toFloat()
    }

    fun eventToWorldY(event: MouseEvent): Float {
        // TODO Update this when panning is implemented
        return (glass.canvas.height - event.y).toFloat()
    }

    fun panBy(dx: Double, dy: Double) {
        allLayers.forEach {
            it.panBy(dx, dy)
        }
    }

    fun draw() {
        allLayers.forEach { it.draw() }
    }
}

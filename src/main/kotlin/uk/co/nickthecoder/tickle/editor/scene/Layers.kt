package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.SceneResource

/**
 */
class Layers(sceneResource: SceneResource, selection: Selection) {

    val stack = StackPane()

    private val allLayers = mutableListOf<Layer>()

    private val stageLayers = mutableListOf<StageLayer>()


    val glass = GlassLayer(sceneResource, selection)

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

    fun worldX(event: MouseEvent): Double {
        return event.x - glass.panX
    }

    fun worldY(event: MouseEvent): Double {
        return glass.canvas.height - event.y - glass.panY
    }

    fun panBy(dx: Double, dy: Double) {
        allLayers.forEach {
            it.panBy(dx, dy)
        }
    }

    fun draw() {
        allLayers.forEach { it.draw() }
    }

    fun currentLayer(): StageLayer? {
        return stageLayers.lastOrNull()
    }

}

package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.editor.EditorAction

/**
 */
class Layers(sceneResource: SceneResource, selection: Selection) {

    val stack = StackPane()

    private val allLayers = mutableListOf<Layer>()

    private val stageLayers = mutableListOf<StageLayer>()

    val glass = GlassLayer(selection)

    private val map = mutableMapOf<String, StageLayer>()

    val stageButton = MenuButton("no stage selected")

    var currentLayer: StageLayer? = null
        set(v) {
            field = v
            if (v == null) {
                stageButton.text = "<no stage selected>"
            } else {
                stageButton.text = v.stageName
                if (singleLayerMode.isSelected) {
                    stageLayers.forEach { it.isLocked = true }
                }
                v.isLocked = false
                v.isVisible = true
            }
        }

    val singleLayerMode = CheckMenuItem("Single Layer Mode")

    init {

        stageButton.graphic = ImageView(EditorAction.imageResource("layers.png"))
        stageButton.items.add(singleLayerMode)
        stageButton.items.add(SeparatorMenuItem())
        sceneResource.sceneStages.forEach { stageName, sceneStage ->
            val layer = StageLayer(sceneResource, stageName, sceneStage)
            add(layer)
            stageLayers.add(layer)
            map[stageName] = layer


            stageButton.items.add(createMenuItem(layer))
            currentLayer = layer
        }
        add(glass)
    }

    fun names(): Collection<String> = map.keys

    fun stageLayer(name: String): StageLayer? = map[name]

    fun editableLayers() = stageLayers.filter { it.isVisible && !it.isLocked }

    fun add(layer: Layer) {
        allLayers.add(layer)
        stack.children.add(layer.canvas)
    }

    fun createMenuItem(layer: StageLayer): MenuItem {
        val menuItem = MenuItem(layer.stageName)
        menuItem.onAction = EventHandler {
            stageButton.text = layer.stageName
            currentLayer = layer
        }
        return menuItem
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
        return currentLayer
    }

}

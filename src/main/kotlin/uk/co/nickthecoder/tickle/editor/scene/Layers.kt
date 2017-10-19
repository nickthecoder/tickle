package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.resources.NoStageConstraint
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.SceneResource
import uk.co.nickthecoder.tickle.resources.StageConstraint

/**
 */
class Layers(sceneResource: SceneResource, selection: Selection) {

    val stack = StackPane()

    private val allLayers = mutableListOf<Layer>()

    private val stageLayers = mutableListOf<StageLayer>()

    val glass = GlassLayer(sceneResource, selection)

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
        sceneResource.stageResources.forEach { stageName, stageResource ->

            val layout = Resources.instance.layouts.find(sceneResource.layoutName)!!
            val constraintName = layout.layoutStages[stageName]?.stageConstraintString
            var constraint: StageConstraint = NoStageConstraint()
            try {
                constraint = Class.forName(constraintName).newInstance() as StageConstraint
            } catch(e: Exception) {
                System.err.println("WARNING : Failed to create the StageConstraint for stage $stageName. Using NoStageConstraint")
            }
            constraint.forStage(stageName, stageResource)

            val layer = StageLayer(sceneResource, stageName, stageResource, constraint)
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

    fun stageLayers(): List<StageLayer> = stageLayers

    fun editableLayers() = stageLayers.filter { it.isVisible && !it.isLocked }

    fun visibleLayers() = stageLayers.filter { it.isVisible }

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

package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.stage.View
import uk.co.nickthecoder.tickle.stage.ZOrderStageView

/**
 */
class Layers(val sceneResource: SceneResource, selection: Selection) {

    val stack = StackPane()

    private val allLayers = mutableListOf<Layer>()

    private val stageLayers = mutableListOf<StageLayer>()

    private val includedLayers = mutableMapOf<String, List<StageLayer>>()

    val glass = GlassLayer(sceneResource, selection)

    private val map = mutableMapOf<String, StageLayer>()

    val stageButton = MenuButton("no stage selected")

    var scale: Double = 1.0
        set(v) {
            field = v
            allLayers.forEach { it.scale(v) }
        }

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

        loadIncludes()

        val layout = Resources.instance.layouts.find(sceneResource.layoutName)

        createStageLayers(sceneResource).forEach { layer ->
            add(layer)
            stageLayers.add(layer)
            map[layer.stageName] = layer

            stageButton.items.add(createMenuItem(layer))
            if (layout?.layoutStages?.get(layer.stageName)?.isDefault == true) {
                currentLayer = layer
            }

        }

        add(glass)
    }

    private fun createStageLayers(sceneResource: SceneResource): List<StageLayer> {
        val result = mutableListOf<StageLayer>()

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

            val layoutView: LayoutView? = layout.layoutViews.values.firstOrNull() { it.stageName == stageName }
            val view: View? = layoutView?.createView()
            val stageView: StageView = if (view is StageView) view else ZOrderStageView()

            val layer = StageLayer(sceneResource, stageName, stageResource, stageView, constraint)
            result.add(layer)
        }

        return result
    }

    private fun loadIncludes() {
        sceneResource.includes.forEach { include ->
            val includedSR = Game.instance.loadScene(include)

            val layers = mutableListOf<StageLayer>()
            includedLayers.put(include.nameWithoutExtension, layers)

            createStageLayers(includedSR).forEach { layer ->
                layer.isLocked = true
                layers.add(layer)
                add(layer)
            }
        }
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


    fun viewX(event: MouseEvent): Double {
        return glass.centerX + (event.x - glass.canvas.width / 2) / scale
    }

    fun viewY(event: MouseEvent): Double {
        return glass.centerY + (glass.canvas.height / 2 - event.y) / scale
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

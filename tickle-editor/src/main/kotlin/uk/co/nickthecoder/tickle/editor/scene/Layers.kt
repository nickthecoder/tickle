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
import uk.co.nickthecoder.tickle.editor.resources.DesignJsonScene
import uk.co.nickthecoder.tickle.editor.resources.DesignSceneResource
import uk.co.nickthecoder.tickle.resources.LayoutView
import uk.co.nickthecoder.tickle.resources.NoStageConstraint
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.StageConstraint
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.stage.View
import uk.co.nickthecoder.tickle.stage.ZOrderStageView

/**
 */
class Layers(val sceneEditor: SceneEditor) {

    val stack = StackPane()

    private val allLayers = mutableListOf<Layer>()

    private val stageLayers = mutableListOf<StageLayer>()

    private val includedLayers = mutableMapOf<String, List<StageLayer>>()

    val glass = GlassLayer(sceneEditor)

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

        val layout = Resources.instance.layouts.find(sceneEditor.sceneResource.layoutName)

        createStageLayers(sceneEditor.sceneResource).forEach { layer: StageLayer ->
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

    private fun createStageLayers(sceneResource: DesignSceneResource): List<StageLayer> {
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
        sceneEditor.sceneResource.includes.forEach { include ->
            val includedSR = DesignJsonScene(include).sceneResource as DesignSceneResource

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

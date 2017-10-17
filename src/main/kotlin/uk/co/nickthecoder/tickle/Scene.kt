package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.stage.FlexPosition
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.stage.View

class Scene {

    var background: Color = Color.black()

    var showMouse: Boolean = true

    val stages = mutableMapOf<String, Stage>()

    private val views = mutableMapOf<String, View>()

    private val autoPositions = mutableMapOf<String, FlexPosition>()

    private var orderedViews: List<View>? = null

    fun stages() = stages.values

    fun views() = views.values

    fun findStage(name: String) = stages[name]

    fun findView(name: String) = views[name]

    fun findStageView(name: String): StageView? {
        val view = findView(name)
        if (view is StageView) {
            return view
        } else {
            return null
        }
    }

    fun layout(width: Int, height: Int) {
        autoPositions.forEach { name, position ->
            views[name]?.let { view ->
                view.rect = position.rect(width, height)
            }
        }
    }

    fun addView(name: String, view: View, position: FlexPosition? = null) {
        views[name] = view
        position?.let {
            autoPositions[name] = it
        }
    }

    fun removeView(name: String) {
        views.remove(name)
        autoPositions.remove(name)
    }

    fun begin() {
        Game.instance.window.showMouse(showMouse)

        stages.values.forEach { stage ->
            stage.begin()
        }

        with(Game.instance.renderer) {
            clearColor(background)
            layout(window.width, window.height)
        }

        orderedViews = views.values.sortedBy { it.zOrder }
    }

    fun activated() {
        stages.values.forEach { stage ->
            stage.activated()
        }
    }

    fun tick() {
        stages.values.forEach { stage ->
            stage.tick()
        }
    }

    fun end() {
        stages.values.forEach { stage ->
            stage.end()
        }
    }

    fun draw(renderer: Renderer) {
        // println("Rendering scene rects=${views.values.map { it.rect }} #actors=${views.values.filterIsInstance<StageView>().map { it.stage.actors.size }}")
        renderer.clear()

        orderedViews?.forEach { view ->
            renderer.beginView()
            view.draw(renderer)
            renderer.endView()
        }
    }

    fun merge(extraScene: Scene) {

        extraScene.stages.forEach { stageName, extraStage ->
            val existingStage = stages[stageName]
            if (existingStage == null) {
                stages[stageName] = extraStage
            } else {
                if (existingStage.javaClass !== extraStage.javaClass) {
                    System.err.println("WARNING. Mering stage $stageName with a stage of the same name, but a different class")
                }
                // TODO Add warning if they have different StageConstraints when StageConstraints are implemented.
                extraStage.actors.forEach { actor ->
                    existingStage.add(actor)
                }
            }
        }
        extraScene.views.forEach { viewName, view ->
            if (view is StageView) {

                if (stages().contains(view.stage)) {
                    if (views.containsKey(viewName)) {
                        System.err.println("WARNING. Duplicate view '$viewName' ignored while merging scene.")
                    } else {
                        addView(viewName, view, extraScene.autoPositions[viewName])
                    }
                } else {
                    // Do nothing. We don't need to include views for stages whose actors have been moved to an
                    // existing stage.
                }

            } else {
                if (!views.containsKey(viewName)) {
                    addView(viewName, view, extraScene.autoPositions[viewName])
                }
            }
        }

    }

}

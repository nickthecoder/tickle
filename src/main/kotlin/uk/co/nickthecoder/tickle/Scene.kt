package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.stage.FlexPosition
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.stage.View

class Scene {

    var background: Color = Color.BLACK

    val stages = mutableMapOf<String, Stage>()

    private val views = mutableMapOf<String, View>()

    private val autoPositions = mutableMapOf<String, FlexPosition>()

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
                println( "Stage View $name positioned at ${view.rect}")
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
        stages.values.forEach { stage ->
            stage.begin()
        }

        with(Game.instance.renderer) {
            clearColor(background)
            layout(window.width, window.height)
        }

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

        // TODO How should these be ordered?
        views.values.forEach { view ->
            renderer.beginView()
            view.draw(renderer)
            renderer.endView()
        }
    }
}

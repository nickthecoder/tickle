package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.physics.TickleWorld
import uk.co.nickthecoder.tickle.resources.ActorXAlignment
import uk.co.nickthecoder.tickle.resources.ActorYAlignment
import uk.co.nickthecoder.tickle.resources.Resources
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

    var world: TickleWorld? = null

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

    /**
     * Called after the scene has been loaded, and producer.sceneLoaded and director.sceneLoaded have been called.
     * Adjusts any actors who's position is not relative to the bottom left.
     * This is useful for games with resizable windows, and actors need to be aligned with the right edge for example.
     *
     * When/If dynamic window resizing is implemented in Tickle, adjustment will also need to take place when a window
     * is resized during the game.
     */
    fun adjustActors(deltaX: Double, deltaY: Double) {

        stages.values.forEach { stage ->
            stage.firstView()?.let { view ->

                val ratioX = view.rect.width / (view.rect.width - deltaX)
                val ratioY = view.rect.height / (view.rect.height - deltaY)

                stage.actors.forEach { actor ->

                    when (actor.xAlignment) {
                        ActorXAlignment.LEFT -> Unit // Do nothing
                        ActorXAlignment.CENTER -> actor.x += deltaX / 2
                        ActorXAlignment.RIGHT -> actor.x += deltaX
                        ActorXAlignment.RATIO -> actor.x = actor.x * ratioX
                    }
                    when (actor.yAlignment) {
                        ActorYAlignment.BOTTOM -> Unit // Do nothing
                        ActorYAlignment.CENTER -> actor.y += deltaY / 2
                        ActorYAlignment.TOP -> actor.y += deltaY
                        ActorYAlignment.RATIO -> actor.y = actor.y * ratioY
                    }
                }
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
        if (Resources.instance.gameInfo.physicsEngine) {
            world = TickleWorld(Resources.instance.gameInfo.gravity)
        }
        stages.values.forEach { stage ->
            stage.actors.forEach { actor ->
                actor.costume.bodyDef?.let { bodyDef ->
                    world?.createBody(bodyDef, actor)
                }
            }
            stage.begin()
        }

        with(Game.instance.renderer) {
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
        world?.tick()
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
        renderer.clearColor(background)
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

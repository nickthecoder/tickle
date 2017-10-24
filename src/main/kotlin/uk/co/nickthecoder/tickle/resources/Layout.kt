package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.Attributes
import uk.co.nickthecoder.tickle.Scene
import uk.co.nickthecoder.tickle.stage.*
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable

class Layout : Deletable, Renamable {

    val layoutStages = mutableMapOf<String, LayoutStage>()
    val layoutViews = mutableMapOf<String, LayoutView>()

    fun createScene(): Scene {
        val scene = Scene()
        layoutStages.forEach { stageName, layoutStage ->
            scene.stages[stageName] = layoutStage.createStage()
        }

        layoutViews.forEach { viewName, layoutView ->
            val view = layoutView.createView()
            view.zOrder = layoutView.zOrder
            if (view is StageView) {
                val stage = scene.stages[layoutView.stageName]
                if (stage == null) {
                    throw IllegalArgumentException("Stage ${layoutView.stageName} not found - cannot create the view")
                } else {
                    view.stage = stage
                    stage.addView(view)
                }
            }
            scene.addView(viewName, view, layoutView.position)
        }

        return scene
    }


    override fun delete() {
        Resources.instance.layouts.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.layouts.rename(this, newName)
    }
}

class LayoutStage {

    var stageString: String = GameStage::class.java.name

    var stageConstraintString: String = NoStageConstraint::class.java.name

    var constraintAttributes = Attributes()

    fun createStage(): Stage {

        try {
            val klass = Class.forName(stageString)
            val newStage = klass.newInstance()
            if (newStage is Stage) {
                return newStage
            } else {
                System.err.println("'$newStage' is not a type of Stage")
            }
        } catch (e: Exception) {
            System.err.println("Failed to create a Stage from : '$stageString'")
        }
        return GameStage()

    }

}

class LayoutView(
        var viewString: String = ZOrderStageView::class.java.name,
        var stageName: String = "") {

    var zOrder: Int = 50

    val position = FlexPosition()


    fun createView(): View {

        try {
            val klass = Class.forName(viewString)
            val newView = klass.newInstance()
            if (newView is View) {
                return newView
            } else {
                System.err.println("'$newView' is not a type of Stage")
            }
        } catch (e: Exception) {
            System.err.println("Failed to create a View from : '$viewString'")
        }
        return ZOrderStageView()

    }
}

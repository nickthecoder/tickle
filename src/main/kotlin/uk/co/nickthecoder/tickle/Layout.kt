package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.stage.*

class Layout() {

    val stages = mutableMapOf<String, LayoutStage>()
    val views = mutableMapOf<String, LayoutView>()

    fun createScene(): Scene {
        val scene = Scene()
        stages.forEach { stageName, layoutStage ->
            scene.stages[stageName] = layoutStage.createStage()
        }

        views.forEach { viewName, layoutView ->
            val view = layoutView.createView()
            if (view is StageView) {
                val stage = scene.stages[layoutView.stageName]
                if (stage == null) {
                    throw IllegalArgumentException("Stage ${layoutView.stageName} not found - cannot create the view")
                } else {
                    view.stage = stage
                }
            }
            scene.addView(viewName, view, layoutView.position)
        }

        return scene
    }

}

class LayoutStage() {
    var stageString: String = GameStage::class.java.name

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
            System.err.println("Failed to create a Role from : '$stageString'")
        }
        return GameStage()

    }

}

class LayoutView(
        var viewString: String = ZOrderStageView::class.java.name,
        var stageName: String = "") {

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
            System.err.println("Failed to create a Role from : '$viewString'")
        }
        return ZOrderStageView()

    }
}

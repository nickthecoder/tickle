package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.stage.*

class Layout() {

    val stages = mutableMapOf<String, LayoutStage>()
    val views = mutableMapOf<String, LayoutView>()

    fun createScene(): Scene {
        val scene = Scene()
        stages.forEach { name, layoutStage ->
            scene.stages[name] = createStage(layoutStage.stageString)
        }

        views.forEach { name, layoutView ->
            val view = createView(layoutView.viewString)
            if (view is StageView) {
                val stage = scene.stages[name]
                if (stage == null) {
                    throw IllegalArgumentException("Stage $name not found - cannot create the view")
                } else {
                    view.stage = stage
                }
            }
            scene.addView(name, view, layoutView.position)
        }

        return scene
    }


    fun createStage(stageString: String): Stage {

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


    fun createView(viewString: String): View {

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

class LayoutStage() {
    var stageString: String = GameStage::class.java.name

}

class LayoutView() {

    var viewString: String = ZOrderStageView::class.java.name
    var stageName: String = ""
    val position = AutoPosition()

}
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
package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.Scene
import uk.co.nickthecoder.tickle.stage.*
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Dependable
import uk.co.nickthecoder.tickle.util.Renamable

class Layout : Deletable, Renamable {

    val layoutStages = mutableMapOf<String, LayoutStage>()
    val layoutViews = mutableMapOf<String, LayoutView>()

    var defaultLayoutStage: LayoutStage?
        get() {
            return layoutStages.values.firstOrNull { it.isDefault == true } ?: layoutStages.values.firstOrNull()
        }
        set(v) {
            layoutStages.values.forEach { it.isDefault = (it === v) }
        }

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

    override fun dependables(): List<Dependable> = SceneStub.allScenesStubs().filter { it.dependsOn(this) }

    override fun delete() {
        Resources.instance.layouts.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.layouts.rename(this, newName)
    }

}

class LayoutStage {

    var stageString: String = GameStage::class.java.name

    var isDefault: Boolean = false

    var stageConstraintString: String = NoStageConstraint::class.java.name

    var constraintAttributes = Resources.instance.createAttributes()

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

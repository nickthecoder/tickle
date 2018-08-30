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

import uk.co.nickthecoder.tickle.NoDirector
import uk.co.nickthecoder.tickle.Scene
import uk.co.nickthecoder.tickle.graphics.Color
import java.io.File

/**
 * Used when loading and editing a Scene. Not used during actual game play.
 */
open class SceneResource {

    var file: File? = null

    var directorString: String = NoDirector::class.java.name

    val directorAttributes = Resources.instance.createAttributes()

    var layoutName: String = ""
        set(v) {
            if (field != v) {
                field = v
                updateLayout()
            }
        }

    var background: Color = Color.black()

    var showMouse: Boolean = true

    /**
     * Names of included scenes. When a scene is started (as part of a running game), the included scenes
     * will be automatically merged together.
     * Within the SceneEditor, the included scenes will be loaded as separate layers, which are displayed, but
     * are not merged, and not editable.
     */
    val includes = mutableListOf<File>()

    /**
     * Keyed on the name of the stage
     */
    val stageResources = mutableMapOf<String, StageResource>()

    /**
     * Gets the Layout to create the scene, and then populates the Stages with Actors.
     */
    fun createScene(): Scene {
        val layout = Resources.instance.layouts.find(layoutName)!!
        val scene = layout.createScene()

        scene.background = background
        scene.showMouse = showMouse

        stageResources.forEach { name, stageResource ->
            val stage = scene.stages[name]
            if (stage == null) {
                System.err.println("ERROR. Stage $name not found. Ignoring all actors on that stage")
            } else {
                stageResource.actorResources.forEach { actorResource ->
                    actorResource.createActor()?.let { actor ->
                        stage.add(actor, false)
                    }
                }
            }
        }

        return scene
    }

    /**
     * Called when the layout has changed. Attempt to move all of the actors from like-names stages, but any
     * unmatched stage names will result in actors being put in a "random" stage.
     */
    private fun updateLayout() {

        val oldStages = stageResources.toMap()
        stageResources.clear()

        val layout = Resources.instance.layouts.find(layoutName)!!
        layout.layoutStages.keys.forEach { stageName ->
            stageResources[stageName] = StageResource()
        }

        oldStages.forEach { stageName, oldStage ->
            if (stageResources.containsKey(stageName)) {
                stageResources[stageName]!!.actorResources.addAll(oldStage.actorResources)
            } else {
                if (oldStage.actorResources.isNotEmpty()) {
                    System.err.println("Warning. Layout ${layoutName} doesn't have a stage called '${stageName}'. Placing actors in another stage.")
                    stageResources.values.firstOrNull()?.actorResources?.addAll(oldStage.actorResources)
                }
            }
        }
    }

}

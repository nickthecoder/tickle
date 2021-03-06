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
package uk.co.nickthecoder.tickle.editor.tabs

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.NoProducer
import uk.co.nickthecoder.tickle.Producer
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.editor.util.ClassParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.editor.util.XYiParameter
import uk.co.nickthecoder.tickle.physics.FilterBits
import uk.co.nickthecoder.tickle.physics.FilterGroups
import uk.co.nickthecoder.tickle.physics.NoFilterBits
import uk.co.nickthecoder.tickle.physics.NoFilterGroups
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.scripts.ScriptManager

class GameInfoTab

    : EditTaskTab(
        GameInfoTask(Resources.instance.gameInfo),
        dataName = "Game Info",
        data = Resources.instance.gameInfo,
        graphicName = "gameInfo.png") {

}

class GameInfoTask(val gameInfo: GameInfo) : AbstractTask() {

    val titleP = StringParameter("title", value = gameInfo.title)
    val idP = StringParameter("ID", value = gameInfo.id)
    val windowSizeP = XYiParameter("windowSize")
    val fullScreenP = BooleanParameter("fullScreen", value = gameInfo.fullScreen)
    val resizableP = BooleanParameter("resizable", value = gameInfo.resizable)

    val initialSceneP = StringParameter("initialScene", value = Resources.instance.sceneFileToPath(gameInfo.initialScenePath))
    val testSceneP = StringParameter("testScene", value = Resources.instance.sceneFileToPath(gameInfo.testScenePath))

    val producerP = ClassParameter("producer", Producer::class.java, value = NoProducer::class.java)

    val physicsEngineP = BooleanParameter("physicsEngine", value = gameInfo.physicsEngine)
    val gravityP = Vector2dParameter("gravity", value = gameInfo.physicsInfo.gravity).asHorizontal()
    val scaleP = DoubleParameter("scale", value = gameInfo.physicsInfo.scale)

    val framesPerSecondP = IntParameter("framesPerSecond", value = gameInfo.physicsInfo.framesPerSecond)
    val velocityIterationsP = IntParameter("velocityIterations", value = gameInfo.physicsInfo.velocityIterations)
    val positionIterationsP = IntParameter("positionIterations", value = gameInfo.physicsInfo.positionIterations)

    val filterGroupsP = GroupedChoiceParameter<Class<*>>("filterGroups", value = NoFilterGroups::class.java, allowSingleItemSubMenus = true)
    val filterBitsP = GroupedChoiceParameter<Class<*>>("filterBits", value = NoFilterBits::class.java, allowSingleItemSubMenus = true)

    val physicsDetailsP = SimpleGroupParameter("physicsDetails")
            .addParameters(gravityP, scaleP, framesPerSecondP, velocityIterationsP, positionIterationsP, filterGroupsP, filterBitsP)
            .asBox()

    override val taskD = TaskDescription("editGameInfo")
            .addParameters(titleP, idP, windowSizeP, resizableP, fullScreenP, initialSceneP, testSceneP, producerP, physicsEngineP, physicsDetailsP)

    init {
        windowSizeP.x = gameInfo.width
        windowSizeP.y = gameInfo.height

        ClassLister.setChoices(filterGroupsP, FilterGroups::class.java)
        ClassLister.setChoices(filterBitsP, FilterBits::class.java)

        try {
            producerP.classValue = ScriptManager.classForName(gameInfo.producerString)
        } catch (e: Exception) {
            System.err.println("Couldn't find class ${gameInfo.producerString}, defaulting to NoProducer")
        }

        try {
            filterGroupsP.value = ScriptManager.classForName(gameInfo.physicsInfo.filterGroupsString)
        } catch (e: Exception) {
            System.err.println("Couldn't find class ${gameInfo.physicsInfo.filterGroupsString}, defaulting to NoFilterGroups")
        }

        try {
            filterBitsP.value = ScriptManager.classForName(gameInfo.physicsInfo.filterBitsString)
        } catch (e: Exception) {
            System.err.println("Couldn't find class ${gameInfo.physicsInfo.filterBitsString}, defaulting to NoFilterBits")
        }

        physicsDetailsP.hidden = physicsEngineP.value != true
        physicsEngineP.listen { physicsDetailsP.hidden = physicsEngineP.value != true }
    }

    override fun run() {

        with(gameInfo) {
            title = titleP.value
            id = idP.value
            width = windowSizeP.x!!
            height = windowSizeP.y!!
            fullScreen = fullScreenP.value == true
            initialScenePath = Resources.instance.scenePathToFile(initialSceneP.value)
            testScenePath = Resources.instance.scenePathToFile(testSceneP.value)
            resizable = resizableP.value!!
            producerString = producerP.classValue!!.name
            physicsEngine = physicsEngineP.value == true
        }

        with(gameInfo.physicsInfo) {
            gravity = gravityP.value
            scale = scaleP.value!!

            framesPerSecond = framesPerSecondP.value!!
            velocityIterations = velocityIterationsP.value!!
            positionIterations = positionIterationsP.value!!

            filterGroupsString = filterGroupsP.value!!.name
            filterBitsString = filterBitsP.value!!.name
        }
    }

}

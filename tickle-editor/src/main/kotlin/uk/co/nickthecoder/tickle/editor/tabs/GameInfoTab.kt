package uk.co.nickthecoder.tickle.editor.tabs

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.NoProducer
import uk.co.nickthecoder.tickle.Producer
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.editor.util.XYiParameter
import uk.co.nickthecoder.tickle.physics.FilterBits
import uk.co.nickthecoder.tickle.physics.FilterGroups
import uk.co.nickthecoder.tickle.physics.NoFilterBits
import uk.co.nickthecoder.tickle.physics.NoFilterGroups
import uk.co.nickthecoder.tickle.resources.Resources

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

    val producerP = GroupedChoiceParameter<Class<*>>("producer", value = NoProducer::class.java, allowSingleItemSubMenus = true)

    val physicsEngineP = BooleanParameter("physicsEngine", value = gameInfo.physicsEngine)
    val gravityP = Vector2dParameter("gravity", value = gameInfo.physicsInfo.gravity).asHorizontal()
    val scaleP = DoubleParameter("scale", value = gameInfo.physicsInfo.scale)
    val velocityIterationsP = IntParameter("velocityIterations", value = gameInfo.physicsInfo.velocityIterations)
    val positionIterationsP = IntParameter("positionIterations", value = gameInfo.physicsInfo.positionIterations)

    val filterGroupsP = GroupedChoiceParameter<Class<*>>("filterGroups", value = NoFilterGroups::class.java, allowSingleItemSubMenus = true)
    val filterBitsP = GroupedChoiceParameter<Class<*>>("filterBits", value = NoFilterBits::class.java, allowSingleItemSubMenus = true)

    val physicsDetailsP = SimpleGroupParameter("physicsDetails")
            .addParameters(gravityP, scaleP, velocityIterationsP, positionIterationsP, filterGroupsP, filterBitsP)
            .asBox()

    override val taskD = TaskDescription("editGameInfo")
            .addParameters(titleP, idP, windowSizeP, resizableP, fullScreenP, initialSceneP, testSceneP, producerP, physicsEngineP, physicsDetailsP)

    init {
        windowSizeP.x = gameInfo.width
        windowSizeP.y = gameInfo.height

        ClassLister.setChoices(producerP, Producer::class.java)
        ClassLister.setChoices(filterGroupsP, FilterGroups::class.java)
        ClassLister.setChoices(filterBitsP, FilterBits::class.java)

        try {
            producerP.value = Class.forName(gameInfo.producerString)
        } catch (e: Exception) {
            System.err.println("Couldn't find class ${gameInfo.producerString}, defaulting to NoProducer")
        }

        try {
            filterGroupsP.value = Class.forName(gameInfo.physicsInfo.filterGroupsString)
        } catch (e: Exception) {
            System.err.println("Couldn't find class ${gameInfo.physicsInfo.filterGroupsString}, defaulting to NoFilterGroups")
        }

        try {
            filterBitsP.value = Class.forName(gameInfo.physicsInfo.filterBitsString)
        } catch (e: Exception) {
            System.err.println("Couldn't find class ${gameInfo.physicsInfo.filterBitsString}, defaulting to NoFilterBits")
        }

        physicsDetailsP.hidden = physicsEngineP.value != true
        physicsEngineP.listen { physicsDetailsP.hidden = physicsEngineP.value != true }
    }

    override fun run() {
        ClassLister.setChoices(producerP, Producer::class.java)

        with(gameInfo) {
            title = titleP.value
            id = idP.value
            width = windowSizeP.x!!
            height = windowSizeP.y!!
            fullScreen = fullScreenP.value == true
            initialScenePath = Resources.instance.scenePathToFile(initialSceneP.value)
            testScenePath = Resources.instance.scenePathToFile(testSceneP.value)
            resizable = resizableP.value!!
            producerString = producerP.value!!.name
            physicsEngine = physicsEngineP.value == true
        }

        with(gameInfo.physicsInfo) {
            gravity = gravityP.value
            scale = scaleP.value!!
            velocityIterations = velocityIterationsP.value!!
            positionIterations = positionIterationsP.value!!
            filterGroupsString = filterGroupsP.value!!.name
            filterBitsString = filterBitsP.value!!.name
        }
    }

}

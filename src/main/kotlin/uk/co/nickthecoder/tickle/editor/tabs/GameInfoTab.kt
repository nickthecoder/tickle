package uk.co.nickthecoder.tickle.editor.tabs

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.NoProducer
import uk.co.nickthecoder.tickle.Producer
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.editor.util.XYiParameter
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
    val windowSizeP = XYiParameter("windowSize")

    val initialSceneP = StringParameter("initialScene", value = Resources.instance.sceneFileToPath(gameInfo.initialScenePath))
    val testSceneP = StringParameter("testScene", value = Resources.instance.sceneFileToPath(gameInfo.testScenePath))

    val resizableP = BooleanParameter("resizable", value = gameInfo.resizable)
    val producerP = ChoiceParameter<Class<*>>("producer", value = NoProducer::class.java)

    override val taskD = TaskDescription("editGameInfo")
            .addParameters(titleP, windowSizeP, resizableP, initialSceneP, testSceneP, producerP)

    init {
        windowSizeP.x = gameInfo.width
        windowSizeP.y = gameInfo.height


        ClassLister.setChoices(producerP, Producer::class.java)
        try {
            producerP.value = Class.forName(gameInfo.producerString)
        } catch (e: Exception) {
            System.err.println("Couldn't find class ${gameInfo.producerString}, defaulting to NoProducer")
        }
    }

    override fun run() {
        ClassLister.setChoices(producerP, Producer::class.java)

        with(gameInfo) {
            title = titleP.value
            width = windowSizeP.x!!
            height = windowSizeP.y!!
            initialScenePath = Resources.instance.scenePathToFile(initialSceneP.value)
            testScenePath = Resources.instance.scenePathToFile(testSceneP.value)
            resizable = resizableP.value!!
            producerString = producerP.value!!.name
        }
    }

}

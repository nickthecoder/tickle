package uk.co.nickthecoder.tickle.editor.tabs

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.NoProducer
import uk.co.nickthecoder.tickle.Producer
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.editor.util.XYiParameter
import uk.co.nickthecoder.tickle.resources.Resources

class GameInfoTab()
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

    val packageInfoP = InformationParameter("packageInfo", information = "The top level packages used by your game. This is used to scan the code for Producer, Director and Role classes.")
    val packagesP = MultipleParameter("packages", label = "Packages", value = gameInfo.packages.toMutableList()) {
        StringParameter("package")
    }
    val noteP = InformationParameter("note", information = "Note. When adding a new package, click the 'Apply' button to refresh the list of Producer classes.")
    val packagesGroupP = SimpleGroupParameter("packagesGroup", label = "Packages")
            .addParameters(packageInfoP, packagesP, noteP)


    override val taskD = TaskDescription("editGameInfo")
            .addParameters(titleP, windowSizeP, resizableP, initialSceneP, testSceneP, producerP, packagesGroupP)

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
        ClassLister.packages(packagesP.value)
        ClassLister.setChoices(producerP, Producer::class.java)

        gameInfo.title = titleP.value
        gameInfo.width = windowSizeP.x!!
        gameInfo.height = windowSizeP.y!!
        gameInfo.initialScenePath = Resources.instance.scenePathToFile(initialSceneP.value)
        gameInfo.testScenePath = Resources.instance.scenePathToFile(testSceneP.value)
        gameInfo.resizable = resizableP.value!!
        gameInfo.producerString = producerP.value!!.name
        gameInfo.packages.clear()
        gameInfo.packages.addAll(packagesP.value)
    }
}


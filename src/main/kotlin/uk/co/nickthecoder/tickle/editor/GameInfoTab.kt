package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.demo.NoProducer
import uk.co.nickthecoder.tickle.demo.Producer

class GameInfoTab() : EditorTab("Game Info", data = Resources.instance.gameInfo) {

    init {
        val taskPane = TaskPane(this, GameInfoTask(Resources.instance.gameInfo))
        content = taskPane.borderPane
    }

}

class GameInfoTask(val gameInfo: GameInfo) : AbstractTask() {

    val titleP = StringParameter("title", value = gameInfo.title)
    val windowSizeP = XYiParameter("windowSize")
    val resizableP = BooleanParameter("resizable", value = gameInfo.resizable)
    val producerP = ChoiceParameter<Class<*>>("producer", value = NoProducer::class.java)

    val packageInfoP = InformationParameter("packageInfo", information = "The top level packages used by your game. This is used to scan the code for Producer, Director and Role classes.")
    val packagesP = MultipleParameter("packages", label = "Packages", value = listOf("uk.co.nickthecoder.tickle")) {
        StringParameter("package")
    }
    val noteP = InformationParameter("note", information = "Note. When adding a new package, click the 'Apply' button to refresh the list of Producer classes.")
    val packagesGroupP = SimpleGroupParameter("packagesGroup", label = "Packages")
            .addParameters(packageInfoP, packagesP, noteP)


    override val taskD = TaskDescription("editGameInfo")
            .addParameters(titleP, windowSizeP, resizableP, producerP, packagesGroupP)

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
        gameInfo.resizable = resizableP.value!!
        gameInfo.producerString = producerP.value!!.name
    }
}


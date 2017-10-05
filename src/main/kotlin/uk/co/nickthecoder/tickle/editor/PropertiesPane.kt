package uk.co.nickthecoder.tickle.editor

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TitledPane
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.SimpleGroupParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.parameters.asVertical
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource

class PropertiesPane : TitledPane() {

    private val noContent = Label()

    private var propertiesPaneContent: PropertiesPaneContent? = null

    fun clear() {
        text = "<none>"
        content = noContent

        MainWindow.instance?.accordion?.expandedPane = MainWindow.instance?.costumesPane
    }

    fun show(ppc: PropertiesPaneContent) {
        propertiesPaneContent?.cleanUp()

        text = ppc.title

        propertiesPaneContent = ppc
        content = ppc.build()

        MainWindow.instance?.accordion?.expandedPane = this
    }
}

interface PropertiesPaneContent {

    val title: String

    fun build(): Node

    fun cleanUp()
}

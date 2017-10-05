package uk.co.nickthecoder.tickle.editor

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.SimpleGroupParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.parameters.asVertical
import uk.co.nickthecoder.tickle.SceneActor

class PropertiesBox {

    val borderPane = BorderPane()

    private val title = Label()

    private val noContent = Label("")

    fun build(): Node {

        borderPane.top = title
        borderPane.center = noContent
        clear()
        return borderPane
    }

    fun clear() {
        title.text = "<none>"
        borderPane.center = noContent
    }

    fun show(sceneActor: SceneActor) {
        title.text = sceneActor.costumeName
        borderPane.center = ActorProperties(sceneActor).build()
    }
}

class ActorProperties(val sceneActor: SceneActor) {

    val xP = DoubleParameter("x", value = sceneActor.x.toDouble())

    val yP = DoubleParameter("y", value = sceneActor.y.toDouble())

    val directionP = DoubleParameter("direction", value = sceneActor.directionDegrees)

    val groupP = SimpleGroupParameter("actorGroup")
            .addParameters(xP, yP, directionP)
            .asVertical()

    init {
        groupP.listen {
            updateSceneActor()
        }
    }

    fun build(): Node {
        val field = groupP.createField()
        return field.control!!
    }

    fun updateSceneActor() {
        xP.value?.let { sceneActor.x = it.toFloat() }
        yP.value?.let { sceneActor.y = it.toFloat() }
        directionP.value?.let { sceneActor.directionDegrees = it }
    }

}

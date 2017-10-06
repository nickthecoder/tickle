package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.SimpleGroupParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.parameters.asVertical
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.editor.PropertiesPaneContent


class ActorProperties(val sceneActor: SceneActor, val sceneResource: SceneResource)

    : PropertiesPaneContent, SceneListerner {

    override val title = sceneActor.costumeName

    val xP = DoubleParameter("x", value = sceneActor.x.toDouble())

    val yP = DoubleParameter("y", value = sceneActor.y.toDouble())

    val directionP = DoubleParameter("direction", value = sceneActor.directionDegrees)

    val attributesP = SimpleGroupParameter("attributes", label = "").asVertical()

    val groupP = SimpleGroupParameter("actorGroup")
            .addParameters(xP, yP, directionP, attributesP)
            .asVertical()

    var dirty = false

    init {
        groupP.listen {
            dirty = true
            Platform.runLater {
                if (dirty) {
                    updateSceneActor()
                }
            }
        }

        sceneActor.attributes.data().forEach { data ->
            data.parameter?.let { it ->
                val parameter = it.copyBounded()
                attributesP.add(parameter)
                try {
                    parameter.stringValue = data.value ?: ""
                } catch (e: Exception) {
                    // Do nothing
                }
            }
        }

        sceneResource.listeners.add(this)
    }


    override fun cleanUp() {
        sceneResource.listeners.remove(this)
    }

    override fun build(): Node {
        val field = groupP.createField()
        return field.control!!
    }

    fun updateSceneActor() {
        xP.value?.let { sceneActor.x = it.toFloat() }
        yP.value?.let { sceneActor.y = it.toFloat() }
        directionP.value?.let { sceneActor.directionDegrees = it }

        sceneResource.fireChange()
        dirty = false
    }

    fun updateParameters() {
        yP.value = sceneActor.y.toDouble()
        xP.value = sceneActor.x.toDouble()
        directionP.value = sceneActor.directionDegrees
    }

    override fun sceneChanged(sceneResource: SceneResource) {
        updateParameters()
    }

}

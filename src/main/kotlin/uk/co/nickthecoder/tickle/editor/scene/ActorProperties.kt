package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource


class ActorProperties(val sceneActor: SceneActor, val sceneResource: SceneResource)

    : PropertiesPaneContent, SceneListerner, ParameterListener {

    override val title = sceneActor.costumeName

    val xP = DoubleParameter("x", value = sceneActor.x.toDouble())

    val yP = DoubleParameter("y", value = sceneActor.y.toDouble())

    val directionP = DoubleParameter("direction", value = sceneActor.direction.degrees)

    val attributesP = SimpleGroupParameter("attributes", label = "").asVertical()

    val groupP = SimpleGroupParameter("actorGroup")
            .addParameters(xP, yP, directionP, attributesP)
            .asVertical()

    var dirty = false

    init {

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

        groupP.parameterListeners.add(this)
        sceneResource.listeners.add(this)
    }

    override fun cleanUp() {
        sceneResource.listeners.remove(this)
        groupP.parameterListeners.remove(this)
    }

    override fun parameterChanged(event: ParameterEvent) {
        dirty = true
        Platform.runLater {
            if (dirty) {
                updateSceneActor()
            }
        }
    }

    override fun sceneChanged(sceneResource: SceneResource) {
        dirty = true
        Platform.runLater {
            if (dirty) {
                updateParameters()
            }
        }
    }

    override fun build(): Node {
        val field = groupP.createField()
        val box = HBox()
        box.children.add(field.controlContainer)
        box.style = "-fx-padding:10px;"
        val scrollPane = ScrollPane(box)
        return scrollPane
    }

    fun updateSceneActor() {
        xP.value?.let { sceneActor.x = it }
        yP.value?.let { sceneActor.y = it }
        directionP.value?.let { sceneActor.direction.degrees = it }

        // Note. We are not updating the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.

        sceneResource.fireChange()
        dirty = false
    }

    fun updateParameters() {
        yP.value = sceneActor.y
        xP.value = sceneActor.x
        directionP.value = sceneActor.direction.degrees

        // Note. We do not update the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.
        dirty = false
    }


}

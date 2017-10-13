package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.ActorResource
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource


class ActorProperties(val actorResource: ActorResource, val sceneResource: SceneResource)

    : PropertiesPaneContent, SceneListerner, ParameterListener {

    override val title = actorResource.costumeName

    val xP = DoubleParameter("x", value = actorResource.x.toDouble())

    val yP = DoubleParameter("y", value = actorResource.y.toDouble())

    val directionP = DoubleParameter("direction", value = actorResource.direction.degrees)

    val textP = StringParameter("text", value = actorResource.text)

    val attributesP = SimpleGroupParameter("attributes", label = "").asVertical()

    val groupP = SimpleGroupParameter("actorGroup")
            .addParameters(xP, yP, directionP, textP, attributesP)
            .asVertical()

    var dirty = false

    init {

        actorResource.attributes.map().keys.sorted().map { actorResource.attributes.getOrCreateData(it) }.forEach { data ->
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
                updateActorResource()
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

    fun updateActorResource() {
        xP.value?.let { actorResource.x = it }
        yP.value?.let { actorResource.y = it }
        directionP.value?.let { actorResource.direction.degrees = it }
        actorResource.text = textP.value

        // Note. We are not updating the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.

        sceneResource.fireChange()
        dirty = false
    }

    fun updateParameters() {
        yP.value = actorResource.y
        xP.value = actorResource.x
        directionP.value = actorResource.direction.degrees
        textP.value = actorResource.text

        textP.hidden = actorResource.pose != null

        // Note. We do not update the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.
        dirty = false
    }


}

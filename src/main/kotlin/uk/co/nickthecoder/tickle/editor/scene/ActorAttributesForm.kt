package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.resources.*


class ActorAttributesForm(val actorResource: ActorResource, val sceneResource: SceneResource)

    : SceneResourceListener, ParameterListener {

    val xP = DoubleParameter("x", value = actorResource.x)

    val yP = DoubleParameter("y", value = actorResource.y)

    val zOrderP = DoubleParameter("zOrder", value = actorResource.zOrder)

    val xAlignmentP = ChoiceParameter<ActorXAlignment>("xAlignment", value = actorResource.xAlignment)
            .enumChoices(true)

    val yAlignmentP = ChoiceParameter<ActorYAlignment>("yAlignment", value = actorResource.yAlignment)
            .enumChoices(true)

    val alignmentGroupP = SimpleGroupParameter("alignment")
            .addParameters(xAlignmentP, yAlignmentP)
            .asHorizontal(LabelPosition.NONE)

    val directionP = DoubleParameter("direction", value = actorResource.direction.degrees)

    val scaleP = DoubleParameter("scale", value = actorResource.scale)

    val textP = StringParameter("text", value = actorResource.text, rows = 3)

    val attributesP = SimpleGroupParameter("attributes", label = "").asVertical()

    val groupP = SimpleGroupParameter("actorGroup")
            .addParameters(attributesP, xP, yP, zOrderP, alignmentGroupP, directionP, scaleP, textP)
            .asVertical()

    var dirty = false

    /**
     * When an actor changes, and I update the parameters, this ignores ParameterChanged events.
     */
    private var ignoreChanges: Boolean = false

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


    fun build(): Node {
        val field = groupP.createField()
        val box = HBox()
        box.children.add(field.controlContainer)
        box.style = "-fx-padding:10px;"
        val scrollPane = ScrollPane(box)
        return scrollPane
    }

    fun cleanUp() {
        sceneResource.listeners.remove(this)
        groupP.parameterListeners.remove(this)
    }

    override fun parameterChanged(event: ParameterEvent) {
        if (!ignoreChanges) {
            dirty = true
            Platform.runLater {
                if (dirty) {
                    updateActorResource()
                }
            }
        }
    }

    override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
        if (actorResource == this.actorResource) {
            dirty = true
            Platform.runLater {
                if (dirty) {
                    ignoreChanges = true
                    updateParameters()
                    ignoreChanges = false
                }
            }
        }
    }

    fun updateActorResource() {
        xP.value?.let { actorResource.x = it }
        yP.value?.let { actorResource.y = it }
        actorResource.xAlignment = xAlignmentP.value!!
        actorResource.yAlignment = yAlignmentP.value!!

        directionP.value?.let { actorResource.direction.degrees = it }
        scaleP.value?.let { actorResource.scale = it }
        actorResource.text = textP.value
        actorResource.zOrder = zOrderP.value!!

        // Note. We are not updating the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.

        sceneResource.fireChange(actorResource, ModificationType.CHANGE)
        dirty = false
    }

    fun updateParameters() {
        yP.value = actorResource.y
        xP.value = actorResource.x
        xAlignmentP.value = actorResource.xAlignment
        yAlignmentP.value = actorResource.yAlignment

        directionP.value = actorResource.direction.degrees
        scaleP.value = actorResource.scale
        textP.value = actorResource.text
        zOrderP.value = actorResource.zOrder

        textP.hidden = actorResource.pose != null

        // Note. We do not update the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.
        dirty = false
    }


}

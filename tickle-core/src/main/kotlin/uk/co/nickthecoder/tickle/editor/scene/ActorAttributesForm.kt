package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.editor.scene.history.ChangedParameter
import uk.co.nickthecoder.tickle.editor.scene.history.ChangedValueParameter
import uk.co.nickthecoder.tickle.editor.util.TextStyleParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.resources.*


class ActorAttributesForm(
        val sceneEditor: SceneEditor,
        val actorResource: ActorResource,
        val sceneResource: SceneResource)

    : SceneResourceListener, ParameterListener {

    val xP = DoubleParameter("x", value = actorResource.x)

    val yP = DoubleParameter("y", value = actorResource.y)

    val zOrderP = DoubleParameter("zOrder", value = actorResource.zOrder)

    val viewAlignmentXP = ChoiceParameter<ActorXAlignment>("viewAlignmentX", value = actorResource.viewAlignmentX)
            .enumChoices(true)

    val viewAlignmentYP = ChoiceParameter<ActorYAlignment>("viewAlignmentY", value = actorResource.viewAlignmentY)
            .enumChoices(true)

    val alignmentGroupP = SimpleGroupParameter("textAlignment", label = "Alignment")
            .addParameters(viewAlignmentXP, viewAlignmentYP)
            .asHorizontal(LabelPosition.NONE)

    val directionP = DoubleParameter("direction", value = actorResource.direction.degrees)

    val scaleP = Vector2dParameter("scale", value = actorResource.scale)

    val textP = StringParameter("text", value = actorResource.text, rows = 3)

    val textStyleP = TextStyleParameter("textStyle")
            .asVertical()

    val sizeP = Vector2dParameter("size", value = actorResource.size)

    val resizeableAlignmentP = Vector2dParameter("resizeableAlignment", label = "Alignment", value = actorResource.sizeAlignment)

    val resizableGroupP = SimpleGroupParameter("resizeable")
            .addParameters(sizeP, resizeableAlignmentP)
            .asPlain()


    val attributesP = SimpleGroupParameter("attributes", label = "").asVertical()

    val groupP = SimpleGroupParameter("actorGroup")
            .addParameters(attributesP, xP, yP, zOrderP, alignmentGroupP, directionP, scaleP)
            .asVertical()

    var dirty = false

    /**
     * When an actor changes, and I update the parameters, this ignores ParameterChanged events.
     */
    private var ignoreChanges: Boolean = false

    init {

        actorResource.textStyle?.let { textStyleP.from(it) }

        if (actorResource.isText()) {
            groupP.addParameters(textP, textStyleP)
        }

        if (actorResource.isSizable()) {
            groupP.addParameters(resizableGroupP)
        }

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

            if (!sceneEditor.history.updating) {

                val inner = event.innerParameter
                val oldValue = event.oldValue

                if (inner is DoubleParameter && oldValue is Double) {
                    sceneEditor.history.makeChange(ChangedParameter(actorResource, inner, oldValue))
                } else if (inner is StringParameter && oldValue is String) {
                    sceneEditor.history.makeChange(ChangedParameter(actorResource, inner, oldValue))
                } else if (inner is IntParameter && oldValue is Int) {
                    sceneEditor.history.makeChange(ChangedParameter(actorResource, inner, oldValue))
                } else if (inner is ValueParameter<*>) {
                    // This uses ValueParameter.coerce, which isn't type safe, and throws if the type of oldValue doesn't match.
                    sceneEditor.history.makeChange(ChangedValueParameter(actorResource, inner, oldValue))
                }
            }

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
        with(actorResource) {
            xP.value?.let { x = it }
            yP.value?.let { y = it }
            viewAlignmentX = viewAlignmentXP.value!!
            viewAlignmentY = viewAlignmentYP.value!!

            directionP.value?.let { direction.degrees = it }
            scale.set(scaleP.value)

            text = textP.value
            textStyle?.let { textStyleP.update(it) }

            zOrder = zOrderP.value!!

            if (actorResource.isSizable()) {
                size.set(sizeP.value)
                sizeAlignment.set(resizeableAlignmentP.value)
            }
        }

        // Note. We are not updating the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.

        sceneResource.fireChange(actorResource, ModificationType.CHANGE)
        dirty = false
    }

    fun updateParameters() {
        with(actorResource) {
            yP.value = y
            xP.value = x
            viewAlignmentXP.value = viewAlignmentX
            viewAlignmentYP.value = viewAlignmentY

            directionP.value = direction.degrees
            if (isSizable()) {
                sizeP.value.set(size)
                resizeableAlignmentP.value.set(sizeAlignment)
            } else {
                scaleP.value.set(scale)
            }
            textP.value = text
            textStyle?.let { textStyleP.from(it) }

            zOrderP.value = zOrder

            textP.hidden = pose != null
            textStyleP.hidden = textStyle == null
        }
        // Note. We do not update the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.
        dirty = false
    }


}

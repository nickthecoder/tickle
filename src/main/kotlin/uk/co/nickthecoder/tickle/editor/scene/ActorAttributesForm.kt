package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
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

    val scaleP = Vector2dParameter("scale", value = actorResource.scale)

    val sizeP = Vector2dParameter("size", value = actorResource.size)

    val flipXP = BooleanParameter("flipX", value = actorResource.flipX)
    val flipYP = BooleanParameter("flipY", value = actorResource.flipY)

    val textP = StringParameter("text", value = actorResource.text, rows = 3)

    val attributesP = SimpleGroupParameter("attributes", label = "").asVertical()

    val groupP = SimpleGroupParameter("actorGroup")
            .addParameters(attributesP, xP, yP, zOrderP, alignmentGroupP, directionP,
                    if (actorResource.isNinePatch()) sizeP else scaleP,
                    flipXP, flipYP, textP)
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
        with(actorResource) {
            xP.value?.let { x = it }
            yP.value?.let { y = it }
            xAlignment = xAlignmentP.value!!
            yAlignment = yAlignmentP.value!!

            directionP.value?.let { direction.degrees = it }
            if (actorResource.isNinePatch()) {
                size.set(sizeP.value)
            } else {
                scale.set(scaleP.value)
            }
            flipX = flipXP.value == true
            flipY = flipYP.value == true
            text = textP.value
            zOrder = zOrderP.value!!
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
            xAlignmentP.value = xAlignment
            yAlignmentP.value = yAlignment

            directionP.value = direction.degrees
            if (isNinePatch()) {
                sizeP.value.set(size)
            } else {
                scaleP.value.set(scale)
            }
            flipXP.value = flipX
            flipYP.value = flipY
            textP.value = text
            zOrderP.value = zOrder

            textP.hidden = pose != null
        }
        // Note. We do not update the dynamic "attributes", because they should ONLY be updated via their
        // Parameters, The scene editor should NOT be changing the string value directly.
        dirty = false
    }


}

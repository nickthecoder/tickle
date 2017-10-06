package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.Attributes
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.editor.PropertiesPaneContent
import uk.co.nickthecoder.tickle.util.Attribute


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

        try {
            sceneActor.costume()?.let { costume ->
                if (costume.roleString.isNotBlank()) {
                    val roleClass = Class.forName(costume.roleString)

                    Attributes.createParameters(roleClass, Attribute::class).forEach { parameter ->
                        attributesP.add(parameter)
                        val attributeName = Attributes.attributeName(parameter)
                        sceneActor.attributes.getValue(attributeName)?.let { value ->
                            try {
                                parameter.stringValue = value
                            } catch (e: Exception) {
                                // Do nothing
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            System.err.println("Problem creating attribute parameters for ${sceneActor.costumeName}.")
            e.printStackTrace()
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


        with(sceneActor.attributes)
        {
            attributesP.children.forEach { child ->
                if (child is ValueParameter<*>) {
                    if (child.value != null) {
                        setValue(Attributes.attributeName(child), child.stringValue)
                    }
                }
            }
        }

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

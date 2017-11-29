package uk.co.nickthecoder.tickle.resources

import javafx.stage.Stage
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.asHorizontal
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.util.rotate

class SnapToOthers : SnapTo {

    var enabled = true

    var closeness = Vector2d(10.0, 10.0)

    override fun snapActor(actorResource: ActorResource, adjustments: MutableList<Adjustment>) {

        if (!enabled) return

        actorResource.layer?.stageResource?.actorResources?.forEach { other ->
            if (other !== actorResource) {

                // Snap the two actors positions
                val dx = actorResource.x - other.x
                val dy = actorResource.y - other.y
                if (dx > -closeness.x && dx < closeness.x && dy > -closeness.y && dy < closeness.y) {
                    adjustments.add(Adjustment(-dx, -dy, Math.abs(dx) + Math.abs(dy)))
                }

                // Snap this actor position with the other actor's snap points
                other.pose?.snapPoints?.forEach { point ->
                    snapPoint(actorResource, other, point, other.pose!!, adjustments)
                }

                // Snap this actor' snap points with the other actor's position
                actorResource.pose?.snapPoints?.forEach { point ->
                    snapPoint2(actorResource, other, point, actorResource.pose!!, adjustments)
                }
            }
        }

    }

    fun snapPoint(actorResource: ActorResource, other: ActorResource, point: Vector2d, pose: Pose, adjustments: MutableList<Adjustment>) {
        val adjustedPoint = Vector2d(point)
        adjustedPoint.x -= pose.offsetX
        adjustedPoint.y -= pose.offsetY

        if (other.direction.radians != pose.direction.radians) {
            adjustedPoint.rotate(other.direction.radians - pose.direction.radians)
        }

        adjustedPoint.x *= other.scale.x
        adjustedPoint.y *= other.scale.y

        val dx = actorResource.x - other.x - adjustedPoint.x
        val dy = actorResource.y - other.y - adjustedPoint.y
        if (dx > -closeness.x && dx < closeness.x && dy > -closeness.y && dy < closeness.y) {
            adjustments.add(Adjustment(-dx, -dy, Math.abs(dx) + Math.abs(dy)))
        }
    }

    fun snapPoint2(actorResource: ActorResource, other: ActorResource, point: Vector2d, pose: Pose, adjustments: MutableList<Adjustment>) {
        val adjustedPoint = Vector2d(point)
        adjustedPoint.x -= pose.offsetX
        adjustedPoint.y -= pose.offsetY

        if (actorResource.direction.radians != pose.direction.radians) {
            adjustedPoint.rotate(actorResource.direction.radians - pose.direction.radians)
        }

        adjustedPoint.x *= actorResource.scale.x
        adjustedPoint.y *= actorResource.scale.y

        val dx = other.x - actorResource.x - adjustedPoint.x
        val dy = other.y - actorResource.y - adjustedPoint.y
        if (dx > -closeness.x && dx < closeness.x && dy > -closeness.y && dy < closeness.y) {
            adjustments.add(Adjustment(dx, dy, Math.abs(dx) + Math.abs(dy)))
        }
    }

    override fun edit() {
        TaskPrompter(SnapToOthersTask()).placeOnStage(Stage())
    }


    inner class SnapToOthersTask : AbstractTask() {

        val enabledP = BooleanParameter("enabled", value = enabled)

        val closenessP = Vector2dParameter("closeness", value = closeness, description = "Snap when this close to a grid marker")
                .asHorizontal()

        override val taskD = TaskDescription("snapToOthers")
                .addParameters(enabledP, closenessP)


        override fun run() {
            enabled = enabledP.value!!
            closeness.set(closenessP.value)
        }

    }

}

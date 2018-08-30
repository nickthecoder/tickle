package uk.co.nickthecoder.tickle.editor.scene

import org.joml.Vector2d
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import uk.co.nickthecoder.paratask.parameters.asHorizontal
import uk.co.nickthecoder.tickle.editor.EditorActions
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter

class SnapToGrid : SnapTo {

    var enabled: Boolean = false

    var spacing = Vector2d(50.0, 50.0)

    var offset = Vector2d(0.0, 0.0)

    var closeness = Vector2d(10.0, 10.0)

    val adjustment = Adjustment()

    override fun snapActor(actorResource: DesignActorResource, adjustments: MutableList<Adjustment>) {

        if (enabled == false) return

        adjustment.reset()

        var dx = (actorResource.x - offset.x) % spacing.x
        var dy = (actorResource.y - offset.y) % spacing.y

        if (dx < 0) dx += spacing.x
        if (dy < 0) dy += spacing.y

        if (dx < closeness.x) {
            adjustment.x = -dx
            adjustment.score = closeness.y + Math.abs(adjustment.x)
        } else if (spacing.x - dx < closeness.x) {
            adjustment.x = spacing.x - dx
            adjustment.score = closeness.y + Math.abs(adjustment.x)
        }

        if (dy < closeness.y) {
            adjustment.y = -dy
            adjustment.score = if (adjustment.score == Double.MAX_VALUE) Math.abs(adjustment.y) + closeness.x else adjustment.score + Math.abs(adjustment.y)
        } else if (spacing.y - dy < closeness.y) {
            adjustment.y = spacing.y - dy
            adjustment.score = if (adjustment.score == Double.MAX_VALUE) Math.abs(adjustment.y) + closeness.x else adjustment.score + Math.abs(adjustment.y)
        }

        if (adjustment.score != Double.MAX_VALUE) {
            adjustments.add(adjustment)
        }
    }

    override fun task() = GridTask()

    override fun toString(): String {
        return "Grid spacing=(${spacing.x},${spacing.y}) offset=(${offset.x}, ${offset.y}) closeness=(${closeness.x},${closeness.y}) enabled=$enabled"
    }

    inner class GridTask : AbstractTask() {

        val enabledP = BooleanParameter("enabled", value = enabled)

        val toggleInfoP = InformationParameter("toggleInfo",
                information = "Note. You can toggle grid snapping using the keyboard shortcut : ${EditorActions.SNAP_TO_GRID_TOGGLE.shortcutLabel() ?: "<NONE>"}\n${snapInfo()}")

        val spacingP = Vector2dParameter("spacing", value = spacing)
                .asHorizontal()

        val closenessP = Vector2dParameter("closeness", value = closeness, description = "Snap when this close to a grid marker")
                .asHorizontal()

        val offsetP = Vector2dParameter("offset", value = offset)
                .asHorizontal()

        override val taskD = TaskDescription("editGrid")
                .addParameters(enabledP, toggleInfoP, spacingP, closenessP, offsetP)

        override fun run() {
            spacing.set(spacingP.value)
            offset.set(offsetP.value)
            closeness.set(closenessP.value)
            enabled = enabledP.value!!

            if (closeness.x > spacing.x / 2) {
                closeness.x = spacing.x / 2
            }
            if (closeness.y > spacing.y / 2) {
                closeness.y = spacing.y / 2
            }
        }
    }

}

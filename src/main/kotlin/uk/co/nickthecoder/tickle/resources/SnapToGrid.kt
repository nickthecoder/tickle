package uk.co.nickthecoder.tickle.resources

import javafx.stage.Stage
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.asHorizontal
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter

class SnapToGrid {

    var enabled: Boolean = false

    var spacing = Vector2d(50.0, 50.0)

    var offset = Vector2d(0.0, 0.0)

    var closeness = Vector2d(10.0, 10.0)


    fun snapActor(actorResource: ActorResource): Boolean {

        var snapped = false

        if (enabled == false) return false

        var dx = (actorResource.x - offset.x) % spacing.x
        var dy = (actorResource.y - offset.y) % spacing.y

        if (dx < 0) dx += spacing.x
        if (dy < 0) dy += spacing.y

        if (dx < closeness.x) {
            actorResource.x -= dx
            snapped = true
        } else if (spacing.x - dx < closeness.x) {
            actorResource.x += spacing.x - dx
            snapped = true
        }

        if (dy < closeness.y) {
            actorResource.y -= dy
            snapped = true
        } else if (spacing.y - dy < closeness.y) {
            actorResource.y += spacing.y - dy
            snapped = true
        }

        return snapped
    }

    fun edit() {
        TaskPrompter(GridTask()).placeOnStage(Stage())
    }

    override fun toString(): String {
        return "Grid spacing=(${spacing.x},${spacing.y}) offset=(${offset.x}, ${offset.y}) closeness=(${closeness.x},${closeness.y}) enabled=$enabled"
    }

    inner class GridTask : AbstractTask() {

        val enabledP = BooleanParameter("enabled", value = enabled)

        val spacingP = Vector2dParameter("spacing", value = spacing)
                .asHorizontal()

        val closenessP = Vector2dParameter("closeness", value = closeness, description = "Snap when this close to a grid marker")
                .asHorizontal()

        val offsetP = Vector2dParameter("offset", value = offset)
                .asHorizontal()

        override val taskD = TaskDescription("editGrid")
                .addParameters(enabledP, spacingP, closenessP, offsetP)

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

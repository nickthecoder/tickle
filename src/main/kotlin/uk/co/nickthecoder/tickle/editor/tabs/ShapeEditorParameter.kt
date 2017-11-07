package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import uk.co.nickthecoder.paratask.parameters.AbstractParameter
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.util.image
import uk.co.nickthecoder.tickle.physics.BoxDef
import uk.co.nickthecoder.tickle.physics.CircleDef
import uk.co.nickthecoder.tickle.physics.ShapeDef

class ShapeEditorParameter(name: String, val pose: Pose)

    : AbstractParameter(name, label = "", description = "") {

    override fun errorMessage(): String? = null

    override fun copy() = ShapeEditorParameter(name, pose)

    override fun isStretchy(): Boolean = true

    private var field: ShapeEditorField? = null

    override fun createField(): ParameterField {
        val result = ShapeEditorField(this)
        result.build()
        field = result
        return result
    }

    fun update(shapedDef: ShapeDef?) {
        field?.update(shapedDef)
    }

}


class ShapeEditorField(shapeEditorParameter: ShapeEditorParameter) : ParameterField(shapeEditorParameter) {

    val pose = shapeEditorParameter.pose
    val width = pose.rect.width
    val height = pose.rect.height

    val margin = 10.0
    val borderColor = Color(0.0, 0.0, 0.0, 0.3)
    val shapeColor = Color(1.0, 0.0, 0.0, 1.0)

    val canvas = Canvas(width.toDouble() + margin * 2, height.toDouble() + margin * 2)

    override fun createControl(): Node {
        update(null)
        return canvas
    }

    fun update(shapeDef: ShapeDef?) {
        with(canvas.graphicsContext2D) {
            save()
            clearRect(0.0, 0.0, width.toDouble() + margin * 2, height.toDouble() + margin * 2)
            lineWidth = 1.0
            stroke = borderColor

            translate(margin, margin)
            strokeRect(0.0, 0.0, width.toDouble(), height.toDouble())

            save()
            translate(pose.offsetX, pose.offsetY)
            when (shapeDef) {
                is CircleDef -> {
                    drawOutlined(shapeColor) {
                        strokeOval(shapeDef.center.x - shapeDef.radius, shapeDef.center.y - shapeDef.radius, shapeDef.radius * 2, shapeDef.radius * 2)
                    }
                }
                is BoxDef -> {
                    translate(shapeDef.center.x, shapeDef.center.y)
                    drawOutlined(shapeColor) {
                        if (shapeDef.roundedEnds) {
                            if (shapeDef.width > shapeDef.height) {
                                val radius = shapeDef.height / 2

                                strokeRect(-shapeDef.width / 2 + radius, -shapeDef.height / 2, shapeDef.width - radius * 2, shapeDef.height)
                                strokeOval(shapeDef.width / 2, -radius, radius * 2, radius * 2)
                                strokeOval(-shapeDef.width / 2, -radius, radius * 2, radius * 2)
                            } else {
                                val radius = shapeDef.width / 2

                                strokeRect(-shapeDef.width / 2, -shapeDef.height / 2 + radius, shapeDef.width, shapeDef.height - radius * 2)
                                strokeOval(-radius, shapeDef.height / 2 - radius * 2, radius * 2, radius * 2)
                                strokeOval(-radius, -shapeDef.height / 2, radius * 2, radius * 2)
                            }
                        } else {
                            strokeRect(-shapeDef.width / 2, -shapeDef.height / 2, shapeDef.width, shapeDef.height)
                        }
                    }
                }
            }

            restore()

            this.globalAlpha = 0.5
            drawImage(pose.image(),
                    pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(),
                    0.0, 0.0, pose.rect.width.toDouble(), pose.rect.height.toDouble())
            this.globalAlpha = 1.0

            restore()
        }
    }

    fun drawOutlined(color: Color, shape: () -> Unit) {
        with(canvas.graphicsContext2D) {
            stroke = Color.BLACK
            lineCap = StrokeLineCap.ROUND
            lineWidth = 4.0
            shape()
            stroke = color
            lineWidth = 2.5
            shape()
        }
    }

}

package uk.co.nickthecoder.tickle

import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.util.Rectd
import uk.co.nickthecoder.tickle.util.string

/**
 * Tiles a single Pose to build an image of the required width and height.
 * This is useful for creating rectangular objects that can have an arbitrary size, such as floors and walls
 * as well as patterned backgrounds.
 *
 * Note that rendering is done by repeatedly drawing the Pose, so if the Pose is small compared to the Actor's size,
 * then it will be somewhat slow. So if you want to tile a small pattern, consider making the Pose have multiple copies
 * of the pattern.
 */
class TiledAppearance(actor: Actor, val pose: Pose) : ResizeAppearance(actor) {

    override val directionRadians: Double
        get() = pose.direction.radians

    private val poseWidth = pose.rect.width

    private val poseHeight = pose.rect.height

    private val partPose = pose.copy()

    init {
        size.x = pose.rect.width.toDouble()
        size.y = pose.rect.height.toDouble()
        oldSize.set(size)

        sizeAlignment.x = pose.offsetX / pose.rect.width
        sizeAlignment.y = pose.offsetY / pose.rect.height
        oldAlignment.set(sizeAlignment)
    }

    override fun draw(renderer: Renderer) {

        //println("Drawing tiled")
        val left = actor.x - sizeAlignment.x * width()
        val bottom = actor.y - sizeAlignment.y * height()

        val simple = actor.isSimpleImage()
        val modelMatrix: Matrix4f?
        if (!simple) {
            modelMatrix = actor.calculateModelMatrix()
        } else {
            modelMatrix = null
        }

        // Draw all of the complete pieces (i.e. where the pose does not need clipping).
        var y = 0.0
        while (y < size.y - poseHeight) {
            var x = 0.0
            while (x < size.x - poseWidth) {
                //println("Drawing whole part from ${pose.rect}")
                drawPart(
                        renderer,
                        left + x, bottom + y, left + x + poseWidth, bottom + y + poseHeight,
                        pose.rectd,
                        modelMatrix)

                x += poseWidth
            }
            y += poseHeight
        }

        val rightEdge = size.x - if (size.x % poseWidth == 0.0) poseWidth.toDouble() else size.x % poseWidth
        val topEdge = y
        val partWidth = size.x - rightEdge
        val partHeight = size.y - topEdge

        //println("Size.x = ${size.x} rightEdge = $rightEdge partWidth = ${partWidth} topEdge = ")
        // Draw the partial pieces on the right edge
        y = 0.0
        partPose.rect.top = pose.rect.top
        partPose.rect.right = (pose.rect.left + partWidth).toInt()
        partPose.updateRectd()
        while (y < size.y - poseHeight) {
            //println("Drawing right edge from ${partPose.rect}")
            drawPart(renderer,
                    left + rightEdge, bottom + y, left + size.x, bottom + y + poseHeight,
                    partPose.rectd,
                    modelMatrix)
            y += poseHeight
        }

        // Draw the partial pieces on the top edge
        var x = 0.0
        partPose.rect.top = (pose.rect.bottom - partHeight).toInt()
        partPose.rect.right = pose.rect.right
        partPose.updateRectd()
        while (x < size.x - poseWidth) {
            //println("Drawing top edge from ${partPose.rect}")
            drawPart(renderer,
                    left + x, bottom + topEdge, left + x + poseWidth, bottom + size.y,
                    partPose.rectd,
                    modelMatrix)
            x += poseWidth
        }

        // Draw the partial piece in the top right corner
        if (rightEdge < size.x && topEdge < size.y) {
            partPose.rect.right = (pose.rect.left + partWidth).toInt()
            partPose.rect.top = (pose.rect.bottom - partHeight).toInt()
            partPose.updateRectd()
            // println("Drawing corner from ${partPose.rect}")
            drawPart(renderer,
                    left + rightEdge, bottom + topEdge, left + size.x, bottom + size.y,
                    partPose.rectd,
                    modelMatrix)
        }

        // println("Done tiled\n")
    }

    fun drawPart(renderer: Renderer, x0: Double, y0: Double, x1: Double, y1: Double, srcRect: Rectd, modelMatrix: Matrix4f?) {
        if (modelMatrix == null) {
            renderer.drawTexture(pose.texture, x0, y0, x1, y1, srcRect, actor.color)
        } else {
            renderer.drawTexture(pose.texture, x0, y0, x1, y1, srcRect, actor.color, modelMatrix)
        }
    }

    override fun toString() = "TiledAppearance pose=$pose size=${size.string()}"
}

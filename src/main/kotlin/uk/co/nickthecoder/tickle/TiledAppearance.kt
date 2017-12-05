package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.util.Rectd

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

    override fun draw(renderer: Renderer) {

        val left = actor.x - alignment.x * width()
        val bottom = actor.y - alignment.y * height()

        var y = 0.0
        var x = 0.0
        while (y < height - poseHeight) {
            x = 0.0
            while (x < width - poseWidth) {
                drawPart(
                        renderer,
                        left + x, bottom + y, left + x + poseWidth, bottom + y + poseHeight,
                        pose.rectd)

                x += poseWidth
            }
            y += poseHeight
        }

        val rightEdge = x
        val topEdge = y
        val partWidth = width - rightEdge
        val partHeight = height - topEdge
        // Draw the partial pieces on the right edge
        y = 0.0
        partPose.rect.top = pose.rect.top
        partPose.rect.right = (partPose.rect.left + partWidth).toInt()
        partPose.updateRectd()
        while (y < height - poseHeight) {
            drawPart(renderer,
                    left + rightEdge, bottom + y, left + width, bottom + y + poseHeight,
                    partPose.rectd)
            y += poseHeight
        }

        // Draw the partial pieces on the top edge
        x = 0.0
        partPose.rect.top = (partPose.rect.bottom - partHeight).toInt()
        partPose.rect.right = pose.rect.right
        partPose.updateRectd()
        while (x < width - poseHeight) {
            drawPart(renderer,
                    left + x, bottom + topEdge, left + x + poseWidth, bottom + height,
                    partPose.rectd)
            x += poseWidth
        }

        // Draw the partial piece in the top right corner
        partPose.rect.right = (partPose.rect.left + partWidth).toInt()
        partPose.updateRectd()
        drawPart(renderer,
                left + rightEdge, bottom + topEdge, left + width, bottom + height,
                partPose.rectd)

    }

    fun drawPart(renderer: Renderer, x0: Double, y0: Double, x1: Double, y1: Double, srcRect: Rectd) {
        if (actor.isSimpleImage()) {
            renderer.drawTexture(pose.texture, x0, y0, x1, y1, srcRect)
        } else {
            TODO("Not implemented")
        }
    }

    override fun toString() = "TiledAppearance pose=$pose size=( $width , $height )"
}

package uk.co.nickthecoder.tickle

import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.graphics.Renderer

data class NinePatch(var pose: Pose, var left: Int, var bottom: Int, var right: Int, var top: Int)

/**
 * Draws an [Actor] using a nine-patch, which allows the actor scaled to any size, and the nine patch
 * will only scale the inner parts. The corners will be draw 1:1.
 *
 * Note. when using nine patches, the Actor's scale is ignored. Use [Actor.resize] to resize.
 * [Actor.flipX] and [Actor.flipY] are also currently ignored.
 */
class NinePatchAppearance(actor: Actor, val ninePatch: NinePatch) : ResizeAppearance(actor) {

    /**
     * A 3x3 array of Poses
     */
    private val pieces = Array<Array<Pose>>(3) {
        Array<Pose>(3) {
            val piecePose = ninePatch.pose.copy()
            piecePose.offsetX = 0.0
            piecePose.offsetY = 0.0
            piecePose

        }
    }

    init {
        val w = ninePatch.pose.rect.width
        val h = ninePatch.pose.rect.height
        val widths = listOf(ninePatch.left, w - ninePatch.left - ninePatch.right, ninePatch.right)
        val heights = listOf(ninePatch.bottom, h - ninePatch.bottom - ninePatch.top, ninePatch.top)

        // Relative to the parent pose.
        val lefts = listOf(0, ninePatch.left, w - ninePatch.right)
        val tops = listOf(h - ninePatch.bottom, ninePatch.top, 0)

        for (x in 0..2) {
            for (y in 0..2) {
                val piecePose = pieces[x][y]

                piecePose.rect.left = ninePatch.pose.rect.left + lefts[x]
                piecePose.rect.right = piecePose.rect.left + widths[x]

                piecePose.rect.top = ninePatch.pose.rect.top + tops[y]
                piecePose.rect.bottom = piecePose.rect.top + heights[y]

                piecePose.updateRectd()
            }
        }
    }

    private val modelMatrix = Matrix4f()

    override val directionRadians: Double
        get() = ninePatch.pose.direction.radians

    override fun draw(renderer: Renderer) {

        val scaleXs = listOf(1f, ((width() - ninePatch.left - ninePatch.right) / pieces[1][0].rect.width).toFloat(), 1f)
        val scaleYs = listOf(1f, ((height() - ninePatch.top - ninePatch.bottom) / pieces[1][0].rect.height).toFloat(), 1f)

        val widths = listOf(ninePatch.left.toDouble(), width() - ninePatch.left - ninePatch.right, ninePatch.right.toDouble())
        val heights = listOf(ninePatch.bottom.toDouble(), height() - ninePatch.top - ninePatch.bottom, ninePatch.top.toDouble())

        val lefts = listOf(0.0, ninePatch.left.toDouble(), (width() - ninePatch.right).toDouble())
        val bottoms = listOf(0.0, ninePatch.bottom.toDouble(), (height() - ninePatch.top).toDouble())

        //println("Drawing nine patch $width , $height widths=$widths heights = $heights")

        for (y in 0..2) {
            for (x in 0..2) {
                val piecePose = pieces[x][y]

                if (piecePose.rect.width > 0 && piecePose.rect.height > 0) {
                    if (actor.isSimpleImage()) {
                        val x0 = actor.x - offsetX() + lefts[x]
                        val y0 = actor.y - offsetY() + bottoms[y]
                        //println("Drawing simple piece $x,$y width=${piecePose.rect.width} height=${piecePose.rect.height} at ( $x0, $y0, ${x0 + widths[x]}, ${y0 + heights[y]} ), ${piecePose.rectd}")
                        renderer.drawTexture(ninePatch.pose.texture, x0, y0, x0 + widths[x], y0 + heights[y], piecePose.rectd, actor.color)

                    } else {
                        modelMatrix.identity()
                        val dx = actor.x + lefts[x]
                        val dy = actor.y + bottoms[y]
                        modelMatrix.identity().translate(dx.toFloat(), dy.toFloat(), 0f)
                        if (actor.direction.radians != directionRadians) {
                            modelMatrix.rotateZ((actor.direction.radians - directionRadians).toFloat())
                        }
                        modelMatrix.scale(scaleXs[x], scaleYs[y], 1f)
                        modelMatrix.translate(-dx.toFloat(), -dy.toFloat(), 0f)

                        val x0 = actor.x - offsetX() + lefts[x]
                        val y0 = actor.y - offsetY() + bottoms[y]
                        //println("Drawing piece $x,$y width=${piecePose.rect.width} height=${piecePose.rect.height} at ( $x0, $y0, ${x0 + widths[x]}, ${y0 + heights[y]} ), ${piecePose.rectd}, ${modelMatrix}")
                        renderer.drawTexture(ninePatch.pose.texture, x0, y0, x0 + widths[x], y0 + heights[y], piecePose.rectd, actor.color, modelMatrix)
                    }
                }
            }
        }
    }

    override fun toString() = "NinePatchAppearance pose=${ninePatch.pose} size=( $width , $height ) margins : ${ninePatch.left}, ${ninePatch.bottom}, ${ninePatch.right}, ${ninePatch.top}"

}

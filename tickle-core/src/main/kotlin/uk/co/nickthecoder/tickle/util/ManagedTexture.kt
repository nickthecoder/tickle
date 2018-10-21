package uk.co.nickthecoder.tickle.util

import org.lwjgl.opengl.GL11.GL_RGBA
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Texture
import java.nio.ByteBuffer

/**
 * A Texture for holding many [Pose]s, which can be dynamically amended
 * i.e. new Poses can be added into spare space within the texture.
 * If there is no suitable spare space, then the texture is made larger.
 *
 * Currently, this can be quite inefficient, as the texture can get fragmented,
 * with lots of unused space. No attempt is made to shuffle the Poses to undo
 * fragmentation.
 * This feature may be added later.
 *
 */
class ManagedTexture(width: Int, height: Int) {

    private val spareSpace = mutableListOf<YDownRect>()

    var texture = Texture(width, height, GL_RGBA, createEmptyBuffer(width, height))

    init {
        spareSpace.add(YDownRect(0, 0, width, height))
    }

    /**
     *
     */
    fun remove(pose: Pose) {
        if (pose.texture != texture) throw IllegalStateException("This pose does not use this texture")
        addSpace(YDownRect(pose.rect.left, pose.rect.top, pose.rect.right, pose.rect.bottom))
    }

    fun add(image: PixelArray): Pose {
        val space = findSpareSpace(image.width, image.height)
        val xGap = space.width - image.width
        val yGap = space.height - image.height

        spareSpace.remove(space)
        val gap1: YDownRect
        val gap2: YDownRect

        // Do we want to cut the spare space horizontally or vertically?
        if (xGap > yGap) {
            // X2
            // 12
            gap1 = YDownRect(space.left, space.top + image.height, space.left + image.width, space.bottom)
            gap2 = YDownRect(space.left + image.width, space.top, space.right, space.bottom)
        } else {
            // X1
            // 22
            gap1 = YDownRect(space.left + image.width, space.top, space.right, space.top + image.height)
            gap2 = YDownRect(space.left, space.top + image.height, space.right, space.bottom)
        }
        if (gap1.width != 0 && gap1.height != 0) {
            spareSpace.add(gap1)
        }
        if (gap2.width != 0 && gap2.height != 0) {
            spareSpace.add(gap2)
        }

        val texturePixels = PixelArray(texture)
        //texturePixels.blit(image)
        // Copy the image data onto texturePixels
        for (y in 0 until image.height) {
            val py = y + space.top
            for (x in 0 until image.width) {
                val px = x + space.left
                texturePixels.setPixel(px, py, image.pixelAt(x, y))
            }
        }
        // Update the texture with the new image
        texture.write(texture.width, texture.height, texturePixels.toBuffer(true))

        // Create a pose
        val rect = YDownRect(space.left, space.top, space.left + image.width, space.top + image.height)
        val pose = Pose(texture, rect)
        return pose
    }

    private fun addSpace(rect: YDownRect) {
        // Can this be merged with an existing space?
        for (ss in spareSpace) {
            val merged = merge(ss, rect)
            if (merged != null) {
                spareSpace.remove(ss)
                // Recurse, as this merged rectangle could be merged with others
                addSpace(merged)
                return
            }
        }
        // Nope, couldn't be merged.
        spareSpace.add(rect)
    }

    private fun merge(a: YDownRect, b: YDownRect): YDownRect? {
        if (a.top == b.top && a.bottom == b.bottom && (a.right == b.left || a.left == b.right)) {
            return YDownRect(Math.min(a.left, b.left), a.top, Math.max(a.right, b.right), a.bottom)
        }
        if (a.left == b.left && a.right == b.right && (a.top == b.bottom || a.bottom == b.top)) {
            return YDownRect(a.left, Math.min(a.top, b.top), a.right, Math.max(a.bottom, b.bottom))
        }
        return null
    }

    private fun findSpareSpace(w: Int, h: Int): YDownRect {
        var best: YDownRect? = null
        var bestGap: Int = Int.MAX_VALUE

        for (ss in spareSpace) {
            val dx = ss.width - w
            val dy = ss.height - h
            if (dx >= 0 && dy >= 0) {
                val gap = Math.max(dx, dy)
                if (gap < bestGap) {
                    best = ss
                    bestGap = gap
                }
            }
        }
        if (best == null) {
            // Damn, there's no suitable gap
            return resize(h)
        } else {
            return best
        }
    }

    private fun resize(extraHeight: Int): YDownRect {
        val newSpace = YDownRect(0, texture.height, texture.width, texture.height + extraHeight)
        spareSpace.add(newSpace)

        val oldArray = texture.read()
        val newArray = ByteArray(texture.width * (texture.height + extraHeight * 4))
        for (i in 0..oldArray.size) {
            newArray[i] = oldArray[i]
        }
        for (i in oldArray.size..newArray.size) {
            newArray[i] = 0
        }
        texture.write(texture.width, texture.height + extraHeight, ByteBuffer.wrap(newArray))
        return newSpace
    }

    companion object {
        fun createEmptyBuffer(width: Int, height: Int): ByteBuffer {
            return ByteBuffer.allocate(width * height * 4)
        }
    }
}

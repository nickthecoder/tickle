package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.CostumeEvent
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Producer

/**
 * Create a set of [Pose]s, where each pose is just *part* of the original.
 * These fragments can then be used to create an explosion effect, when an Actor dies, it is
 * replaced by a set of Actors, one for each fragment, which head out on their own trajectories.
 *
 * [Producer.begin] is a good place to call this from.
 *
 * The source pose is fragmented using a "mask", where each pixel is colour coded to indicate
 * which pixels belong to each fragment.
 * e.g. If you want three fragments, then use a pose with just red, green and blue pixels
 * (the actual colours don't matter).
 * The alpha channel is ignored, and it is often nice to use the same alpha channel as the source pose.
 * When creating this colour mask, be sure that each pixel is EXACTLY the right colour. This means you
 * cannot use a "fuzzy" brush.
 *
 * While it is usual for each fragment to be made up of a single contiguous block of pixels,
 * this isn't required. So if you use a mask with two separate blobs of blue pixels, these will
 * form ONE fragment.
 *
 * When to call FragmentMaker :
 * - At runtime, when you game begins (in Director.begin).
 *   The created poses won't appear in the Tickle Editor.
 *   This is simple, but does add some time to the game's startup.
 * - At design time, from an FXCoder script.
 *   The generated poses will become part of your tickle resources.
 *   No additional start-up time, but if you change the source pose, then you'll need to run the
 *   FragmentMaker again. This can get annoying if you update your graphics a lot.
 * - A combined approach.
 *   While developing your game, call it from Director.begin, and then when your game is nearly finished,
 *   remove from Director.begin and use FXCoder instead.
 */
class FragmentMaker(
        val sourcePose: Pose,
        val maskPose: Pose) {

    constructor(costume: Costume, sourceEventName: String = "default", maskEventName: String = "fragmentMask") :
            this(costume.choosePose(sourceEventName)!!, costume.choosePose(maskEventName)!!)

    var texture: ManagedTexture? = null

    fun generate(): List<FragmentInfo> {

        if (maskPose.rect.width > sourcePose.rect.width) throw IllegalArgumentException("The mask is wider than the source")
        if (maskPose.rect.height > sourcePose.rect.height) throw IllegalArgumentException("The mask is taller than the source")

        val width = sourcePose.rect.width
        val height = sourcePose.rect.height

        val source = PixelArray(sourcePose.texture)
        val mask = PixelArray(maskPose.texture)

        fun sourcePixelAt(x: Int, y: Int): Int {
            return source.pixelAt(x + sourcePose.rect.left, y + sourcePose.rect.top)
        }

        fun maskColorAt(x: Int, y: Int): Int {
            return mask.colorAt(x + maskPose.rect.left, y + maskPose.rect.top)
        }

        // Create FragmentInfo for each unique color in the mask image
        val infoMap = mutableMapOf<Int, FragmentInfo>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = maskColorAt(x, y)
                if (color != 0) {
                    val info = infoMap[color]
                    if (info == null) {
                        infoMap[color] = FragmentInfo(color, x, y)
                    } else {
                        info.update(x, y)
                    }
                }
            }
        }

        var texture = texture
        if (texture == null) {
            texture = ManagedTexture(width * infoMap.size, height)
            this.texture = texture
        }

        // Create poses
        for (info in infoMap.values) {

            val dest = PixelArray(info.width, info.height)
            println("Created dest array ${info.width} x ${info.height}")

            for (y in 0 until dest.height) {
                val sourceY = y + info.minY
                for (x in 0 until dest.width) {
                    val sourceX = x + info.minX
                    if (maskColorAt(sourceX, sourceY) == info.color) {
                        dest.setPixel(x, y, sourcePixelAt(sourceX, sourceY))
                    } else {
                        dest.setAlpha(x, y, 0)
                    }
                }
            }
            val pose = texture.add(dest)
            //pose.offsetX = Math.round(info.centerX).toDouble() - info.minX
            //pose.offsetY = pose.rect.height - (Math.round(info.centerY).toDouble() - info.minY)
            //pose.snapPoints.add(Vector2d(sourcePose.offsetX - info.minX, sourcePose.offsetY - (sourcePose.rect.height - info.maxY)))

            // Make the offsets such that the fragment will appear in the same place as the source when added to the stage.
            pose.offsetX = sourcePose.offsetX - info.minX
            pose.offsetY = sourcePose.offsetY - (sourcePose.rect.height - info.maxY)
            // Add a snap point at the center of gravity (ish).
            // It is likely that once the fragment actor has been created, its PoseAppearance's offset will be
            // changed to this snap point, so that rotation will look sensible.
            pose.snapPoints.add(Vector2d(Math.round(info.centerX).toDouble() - info.minX, pose.rect.height - (Math.round(info.centerY).toDouble() - info.minY)))
            info.pose = pose
        }

        return infoMap.values.toList()
    }

    class FragmentInfo(val color: Int, var minX: Int, var minY: Int) {
        var pose: Pose? = null

        /**
         * Relative to the source pose (from the TOP)
         */
        var maxX: Int = minX
        var maxY = minY

        val width: Int
            get() = maxX - minX + 1

        val height: Int
            get() = maxY - minY + 1

        private var sumX: Double = 0.0
        private var sumY: Double = 0.0
        private var pixelCount = 0

        /**
         * Relative to the source pose (from the TOP)
         */
        val centerX
            get() = sumX / pixelCount
        val centerY
            get() = sumY / pixelCount

        fun update(x: Int, y: Int) {
            sumX += x
            sumY += y
            pixelCount++
            if (x < minX) minX = x
            if (x > maxX) maxX = x
            if (y < minY) minY = y
            if (y > maxY) maxY = y
        }
    }

    companion object {

        @JvmStatic
        fun addToCostume(poses: Collection<Pose>, costume: Costume, fragmentEventName: String = "fragments") {
            var event = costume.events[fragmentEventName]
            if (event == null) {
                event = CostumeEvent()
                costume.events[fragmentEventName] == event
            }

            event.poses.addAll(poses)
        }

        @JvmStatic
        fun randomMask(pose: Pose, fragmentCount: Int, alphaThreshold: Int = 1): PixelArray {

            val width = pose.rect.width
            val height = pose.rect.height
            val source = PixelArray(pose.texture)
            val dest = PixelArray(width, height)

            val neighbours = List(fragmentCount) { mutableListOf<Pair<Int, Int>>() }

            // Create seeds points.
            for (n in 0 until fragmentCount) {
                var x = Rand.randomInt(width)
                var y = Rand.randomInt(height)
                // Don't let the same seed point be used for two fragments.
                // The seed points cannot be on transparent pixels
                while (dest.colorAt(x, y) != 0 || source.alphaAt(x + pose.rect.left, y + pose.rect.top) < alphaThreshold) {
                    x = Rand.randomInt(width)
                    y = Rand.randomInt(height)
                }
                // Change the pixel color to mark that it is being used.
                dest.setColor(x, y, 1)
                neighbours[n].add(Pair(x, y))
            }
            // Reset the seed points color
            for (n in 0 until fragmentCount) {
                val (x, y) = neighbours[n][0]
                dest.setColor(x, y, 0)
            }

            fun listAdd(list: MutableList<Pair<Int, Int>>, x: Int, y: Int) {
                if (x >= 0 && y >= 0 && x < width && y < height
                        && dest.colorAt(x, y) == 0 //0x006600
                        && source.alphaAt(x + pose.rect.left, y + pose.rect.top) >= alphaThreshold) {
                    list.add(Pair(x, y))
                }
            }

            // Flood fill from the seed points till the whole image is filled.
            var done = 0
            var pixelsSet = 0
            while (done < fragmentCount) {
                done = 0
                for (n in 0 until fragmentCount) {
                    val list = neighbours[n]
                    if (list.isEmpty()) {
                        done++
                    } else {
                        val i = Rand.randomInt(list.size)
                        val (x, y) = list[i]
                        list.removeAt(i)
                        if (dest.colorAt(x, y) == 0) {
                            pixelsSet++
                            dest.setColor(x, y, 1 + n * 255 / fragmentCount)
                            dest.setAlpha(x, y, source.alphaAt(x + pose.rect.left, y + pose.rect.top))

                            // Add neighbours if they aren't already being used.
                            listAdd(list, x - 1, y)
                            listAdd(list, x + 1, y)
                            listAdd(list, x, y - 1)
                            listAdd(list, x, y + 1)
                        }
                    }
                }
            }

            return dest
        }

        /**
         * Generates a randomly generated Pose suitable for the colorMask of [FragmentMaker].
         * Note, the new pose is added to a new Texture, so dispose of this texture when you've finished with it.
         *
         * This is quite inefficient, so it is slow for large Poses. Sorry.
         * Don't use this during game play!
         */
        @JvmStatic
        fun randomMaskPose(pose: Pose, fragmentCount: Int, alphaThreshold: Int = 1): Pose {
            val texture = randomMask(pose, fragmentCount, alphaThreshold).toTexture()
            return Pose(texture, YDownRect(0, 0, texture.width, texture.height))
        }

    }

}

package uk.co.nickthecoder.tickle.util

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
        val colorMask: Pose) {

    constructor(costume: Costume, sourceEventName: String = "default", maskEventName: String = "fragmentMask") :
            this(costume.choosePose(sourceEventName)!!, costume.choosePose(maskEventName)!!)

    var texture: ManagedTexture? = null

    fun generate(): List<Pose> {

        val width = sourcePose.rect.width
        val height = sourcePose.rect.height

        val source = PixelArray(sourcePose.texture)

        fun colorAt(x: Int, y: Int): Int {
            return source.colorAt(x + sourcePose.rect.left, y + sourcePose.rect.top)
        }

        fun pixelAt(x: Int, y: Int): Int {
            return source.pixelAt(x + sourcePose.rect.left, y + sourcePose.rect.top)
        }

        // Create FragmentInfo for each unique color in the mask image
        val infoMap = mutableMapOf<Int, FragmentInfo>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = colorAt(x, y)
                val info = infoMap[color]
                if (info == null) {
                    infoMap[color] = FragmentInfo(color, x, y)
                } else {
                    info.update(x, y)
                }
            }
        }

        var texture = texture
        if (texture == null) {
            texture = ManagedTexture(width * infoMap.size, height)
            this.texture = texture
        }

        val poses = mutableListOf<Pose>()

        for (info in infoMap.values) {

            val dest = PixelArray(info.width, info.height)
            for (y in 0 until info.height) {
                val ty = y + info.minY
                for (x in 0 until info.width) {
                    val tx = x + info.minX
                    if (colorAt(x + info.minX, y + info.minY) == info.color) {
                        dest.setPixel(tx, ty, pixelAt(tx, ty))
                    } else {
                        dest.setAlpha(tx, ty, 0)
                    }
                }
            }
            val pose = texture.add(dest)
            poses.add(pose)
        }

        return poses
    }

    class FragmentInfo(val color: Int, var minX: Int, var minY: Int) {
        var maxX: Int = minX
        var maxY = minY

        val width: Int
            get() = maxX - minX + 1

        val height: Int
            get() = maxY - minY + 1

        fun update(x: Int, y: Int) {
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

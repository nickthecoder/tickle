package uk.co.nickthecoder.tickle.overlap

import uk.co.nickthecoder.tickle.Actor

/**
 * Uses [PixelOverlapping], but with a threshold value, so that the fuzzy edges of images do not cause objects
 * to be considered overlapping, when they appear to be a pixel or two apart.
 *
 * We compare the alpha channel of the result of overlapping the two images with the threshold. If any pixel is
 * above the threshold, then the Actors are considered to be touching.
 * A threshold of zero means they are overlapping if any pixels that are event slightly opaque overlap.
 * In practice, 0 is a bad default, but the "perfect" default value isn't obvious!
 */
class ThresholdPixelOverlapping(val threshold: Int, val pixelOverlap: PixelOverlapping)
    : Overlapping {

    override fun overlapping(actorA: Actor, actorB: Actor): Boolean {
        return pixelOverlap.overlapping(actorA, actorB, threshold)
    }
}

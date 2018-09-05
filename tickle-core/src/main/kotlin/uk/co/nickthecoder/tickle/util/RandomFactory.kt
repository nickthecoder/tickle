/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector4d
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp
import uk.co.nickthecoder.tickle.graphics.Color
import java.util.*

class RandomFactory(seed: Long?) {

    constructor() : this(null)

    val random = if (seed == null) Random() else Random(seed)

    /**
     * Returns a random number greater than or equals to 0 and less than 1.
     */
    fun nextDouble() = random.nextDouble()

    fun nextDouble(ease: Ease = LinearEase.instance) = ease.ease(random.nextDouble())
    /**
     * Returns a random number greater than or equals to 0 and less than 1.
     */
    fun nextFloat() = random.nextDouble().toFloat()

    fun nextFloat(ease: Ease) = ease.ease(random.nextDouble()).toFloat()


    /**
     * Returns a random integer from 0 to max - 1.
     */
    fun randomInt(max: Int) = random.nextInt(max)

    /**
     * If max is +ve, returns a random number greater than or equal to 0 and less than max.
     * If max is -ve, returns a random number greater than max and less than or equal to 0.
     */
    fun randomDouble(max: Double) = random.nextDouble() * max

    /**
     * If max is +ve, returns a random number greater than or equal to 0 and less than max.
     * If max is -ve, returns a random number greater than max and less than or equal to 0.
     */
    fun randomFloat(max: Float) = random.nextFloat() * max

    /**
     * Returns true 1 in [n] times.
     */
    fun oneIn(n: Int) = random.nextInt(n) == 0

    /**
     * Returns a number in the range -[limit] to [limit].
     */
    fun plusMinus(limit: Double) = nextDouble() * limit * 2 - limit

    fun plusMinus(limit: Double, ease: Ease) = nextDouble(ease) * limit * 2 - limit

    fun plusMinus(limit: Float): Float = (nextDouble() * limit * 2 - limit).toFloat()

    fun plusMinus(limit: Float, ease: Ease): Float = (nextDouble(ease) * limit * 2 - limit).toFloat()

    /**
     * Returns a number in the range -[limit] to [limit].
     */
    fun between(from: Double, to: Double) = lerp(from, to, nextDouble())

    fun between(from: Double, to: Double, ease: Ease) = lerp(from, to, nextDouble(ease))

    fun between(from: Float, to: Float) = lerp(from, to, nextFloat())

    fun between(from: Float, to: Float, ease: Ease) = lerp(from, to, nextFloat(ease))

    /**
     * Returns a color linearly interpolated between the two given colors.
     */
    fun between(from: Color, to: Color) = between(from, to, LinearEase.instance)

    fun between(from: Color, to: Color, ease: Ease): Color {
        val result = Color()
        from.lerp(to, nextDouble(ease).toFloat(), result)
        return result
    }

    /**
     * Returns a Polar2d coordinate between the [from] and [to].
     * A single random number is chosen from 0..1, and this is used to linearly interpolate both the
     * magnitude and the direction. Therefore the possible results will lie on a curve (not a straight line).
     */
    fun between(from: Polar2d, to: Polar2d) = Polar2d(from).lerp(to, nextDouble())

    fun between(from: Polar2d, to: Polar2d, ease: Ease) = Polar2d(from).lerp(to, nextDouble(ease))

    /**
     * Returns a vector randomly distributed along the line between [from] and [to].
     */
    fun between(from: Vector2d, to: Vector2d): Vector2d = Vector2d(from).lerp(to, nextDouble())

    fun between(from: Vector2d, to: Vector2d, ease: Ease): Vector2d = Vector2d(from).lerp(to, nextDouble(ease))

    /**
     * Returns a vector randomly distributed along the line between [from] and [to].
     */
    fun between(from: Vector3d, to: Vector3d): Vector3d = Vector3d(from).lerp(to, nextDouble())

    fun between(from: Vector3d, to: Vector3d, ease: Ease): Vector3d = Vector3d(from).lerp(to, nextDouble(ease))

    /**
     * Returns a vector randomly distributed along the line between [from] and [to].
     */
    fun between(from: Vector4d, to: Vector4d): Vector4d = Vector4d(from).lerp(to, nextDouble())

    fun between(from: Vector4d, to: Vector4d, ease: Ease): Vector4d = Vector4d(from).lerp(to, nextDouble(ease))

    /**
     * Returns a randomly selected item from the list.
     */
    fun item(list: List<*>): Any? {
        return list[randomInt(list.size)]
    }

    companion object {

        /**
         * A shared instance.
         */
        val instance = RandomFactory()
    }

}

/**
 * Returns a randomly selected item from the list.
 */
inline fun <reified T> RandomFactory.randomListItem(list: List<T>): T {
    return list[randomInt(list.size)]
}

/**
 * Static methods using a shared instance of RandomFactory
 */
object Rand {
    inline fun <reified T> item(list: List<T>) = RandomFactory.instance.item(list)

    @JvmStatic fun nextInt(max: Int) = RandomFactory.instance.randomInt(max)
    @JvmStatic fun oneIn(n: Int) = RandomFactory.instance.oneIn(n)

    @JvmStatic fun plusMinus(limit: Double, ease: Ease = LinearEase.instance) = RandomFactory.instance.plusMinus(limit, ease)
    @JvmStatic fun plusMinus(limit: Float, ease: Ease = LinearEase.instance) = RandomFactory.instance.plusMinus(limit, ease)

    @JvmStatic fun between(from: Double, to: Double, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    @JvmStatic fun between(from: Float, to: Float, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    @JvmStatic fun between(from: Color, to: Color, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    @JvmStatic fun between(from: Polar2d, to: Polar2d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    @JvmStatic fun between(from: Vector2d, to: Vector2d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    @JvmStatic fun between(from: Vector3d, to: Vector3d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    @JvmStatic fun between(from: Vector4d, to: Vector4d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
}

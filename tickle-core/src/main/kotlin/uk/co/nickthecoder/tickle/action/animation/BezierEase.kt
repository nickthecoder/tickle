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
/*
 * Copyright MPL2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This code was originally based upon nsSMILKeySpline.cpp, part of the Mozilla code base.
 *
 * Here's a link to nsSMILKeySpline.cpp
 *
 * https://dxr.mozilla.org/mozilla-central/source/dom/smil/nsSMILKeySpline.cpp
 *
 * I used a much older version when converting it to Java, and then many years later converted the Java version to Kotlin.
 *
 * My old Java code says that nsSMILKeySpline.cpp was written by Brian Birtles, but the code linked to above
 * contains no author information.
 *
 * Kotlin version written by Nick Robinson.
 */

package uk.co.nickthecoder.tickle.action.animation

class BezierEase(
        private val mX1: Double,
        private val mY1: Double,
        private val mX2: Double,
        private val mY2: Double)

    : Ease {

    internal var mSampleValues: DoubleArray

    init {
        this.mSampleValues = DoubleArray(kSplineTableSize)
        if (this.mX1 != this.mY1 || this.mX2 != this.mY2) {
            calcSampleValues()
        }

    }

    override fun ease(t: Double): Double {
        return getSplineValue(t)
    }

    private fun getSplineValue(aX: Double): Double {
        if (this.mX1 == this.mY1 && this.mX2 == this.mY2) {
            return aX
        }

        return calcBezier(getTForX(aX), this.mY1, this.mY2)
    }

    private fun calcSampleValues() {
        for (i in 0..kSplineTableSize - 1) {
            this.mSampleValues[i] = calcBezier(i * kSampleStepSize, this.mX1, this.mX2)
        }
    }

    internal fun calcBezier(aT: Double, aA1: Double, aA2: Double): Double {
        // use Horner's scheme to evaluate the Bezier polynomial
        return ((A(aA1, aA2) * aT + B(aA1, aA2)) * aT + C(aA1)) * aT
    }

    private fun getTForX(aX: Double): Double {

        // Find interval where t lies
        var intervalStart = 0.0
        var currentSampleIndex = 1
        val lastSampleIndex = kSplineTableSize - 1
        while (currentSampleIndex != lastSampleIndex && this.mSampleValues[currentSampleIndex] <= aX) {
            intervalStart += kSampleStepSize
            currentSampleIndex += 1
        }

        currentSampleIndex -= 1 // t now lies between *currentSample and *currentSample+1

        // Interpolate to provide an initial guess for t
        val dist = (aX - this.mSampleValues[currentSampleIndex]) / (this.mSampleValues[currentSampleIndex + 1] - this.mSampleValues[currentSampleIndex])
        val guessForT = intervalStart + dist * kSampleStepSize

        // Check the slope to see what strategy to use. If the slope is too small
        // Newton-Raphson iteration won't converge on a root so we use bisection
        // instead.
        val initialSlope = getSlope(guessForT, this.mX1, this.mX2)
        if (initialSlope >= NEWTON_MIN_SLOPE) {
            return newtonRaphsonIterate(aX, guessForT)
        } else if (initialSlope == 0.0) {
            return guessForT
        } else {
            return binarySubdivide(aX, intervalStart, intervalStart + kSampleStepSize)
        }
    }

    private fun newtonRaphsonIterate(aX: Double, aGuessT: Double): Double {
        var aGuessTvar = aGuessT
        // Refine guess with Newton-Raphson iteration
        for (i in 0..NEWTON_ITERATIONS - 1) {
            // We're trying to find where f(t) = aX,
            // so we're actually looking for a root for: CalcBezier(t) - aX
            val currentX = calcBezier(aGuessTvar, this.mX1, this.mX2) - aX
            val currentSlope = getSlope(aGuessTvar, this.mX1, this.mX2)

            if (currentSlope == 0.0) {
                return aGuessTvar
            }

            aGuessTvar -= currentX / currentSlope
        }

        return aGuessTvar
    }

    private fun binarySubdivide(aX: Double, aA: Double, aB: Double): Double {
        var aAvar = aA
        var aBvar = aB
        var currentX: Double
        var currentT = 0.0
        var i = 0

        while (i < SUBDIVISION_MAX_ITERATIONS) {
            currentT = aAvar + (aBvar - aAvar) / 2f
            currentX = calcBezier(currentT, this.mX1, this.mX2) - aX

            if (currentX > 0f) {
                aBvar = currentT
            } else {
                aAvar = currentT
            }
            if (Math.abs(currentX) < SUBDIVISION_PRECISION) {
                break
            }
            i++
        }
        return currentT
    }

    companion object {

        private val NEWTON_ITERATIONS = 4
        private val NEWTON_MIN_SLOPE = 0.02f
        private val SUBDIVISION_PRECISION = 0.0000001f
        private val SUBDIVISION_MAX_ITERATIONS = 10

        private val kSplineTableSize = 11
        private val kSampleStepSize = 1.0 / (kSplineTableSize - 1)

        private fun A(aA1: Double, aA2: Double): Double {
            return 1f - 3f * aA2 + 3f * aA1
        }

        private fun B(aA1: Double, aA2: Double): Double {
            return 3f * aA2 - 6f * aA1
        }

        private fun C(aA1: Double): Double {
            return 3f * aA1
        }

        private fun getSlope(aT: Double, aA1: Double, aA2: Double): Double {
            return 3f * A(aA1, aA2) * aT * aT + 2f * B(aA1, aA2) * aT + C(aA1)
        }
    }

}

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
package uk.co.nickthecoder.tickle.action.animation

object Eases {

    @JvmStatic val linear: Ease = LinearEase.instance
    @JvmStatic val defaultEase: Ease = BezierEase(0.250, 0.100, 0.250, 1.000)

    @JvmStatic val easeIn: Ease = BezierEase(0.420, 0.000, 1.000, 1.000)
    @JvmStatic val easeInQuad: Ease = BezierEase(0.550, 0.085, 0.680, 0.530)
    @JvmStatic val easeInCubic: Ease = BezierEase(0.895, 0.030, 0.685, 0.220)
    @JvmStatic val easeInExpo: Ease = BezierEase(0.950, 0.050, 0.795, 0.035)
    @JvmStatic val easeInCirc: Ease = BezierEase(0.600, 0.040, 0.980, 0.335)
    @JvmStatic val easeInBack: Ease = BezierEase(0.610, -0.255, 0.730, 0.015)

    @JvmStatic val easeOut: Ease = BezierEase(0.000, 0.000, 0.580, 1.000)
    @JvmStatic val easeOutQuad: Ease = BezierEase(0.250, 0.460, 0.450, 0.940)
    @JvmStatic val easeOutCubic: Ease = BezierEase(0.215, 0.610, 0.355, 1.000)
    @JvmStatic val easeOutExpo: Ease = BezierEase(0.190, 1.000, 0.220, 1.000)
    @JvmStatic val easeOutCirc: Ease = BezierEase(0.075, 0.820, 0.165, 1.000)
    @JvmStatic val easeOutBack: Ease = BezierEase(0.175, 0.885, 0.320, 1.275)

    @JvmStatic val easeInOut: Ease = BezierEase(0.420, 0.000, 0.580, 1.000)
    @JvmStatic val easeInOutQuad: Ease = BezierEase(0.455, 0.030, 0.515, 0.955)
    @JvmStatic val easeInOutCubic: Ease = BezierEase(0.645, 0.045, 0.355, 1.000)
    @JvmStatic val easeInOutExpo: Ease = BezierEase(1.000, 0.000, 0.000, 1.000)
    @JvmStatic val easeInOutCirc: Ease = BezierEase(0.785, 0.135, 0.150, 0.860)
    @JvmStatic val easeInOutBack: Ease = BezierEase(0.680, -0.550, 0.265, 1.550)


    @JvmStatic val bounce: Ease = CompoundEase()
            .addEase(easeInQuad, 1.0, 1.0)
            .addEase(easeOutQuad, 0.2, 0.8)
            .addEase(easeInQuad, 0.2, 1.0)

    @JvmStatic val bounce2: Ease = CompoundEase()
            .addEase(easeInQuad, 1.0, 1.0)
            .addEase(easeOutQuad, 0.2, 0.8)
            .addEase(easeInQuad, 0.2, 1.0)
            .addEase(easeOutQuad, 0.1, 0.95)
            .addEase(easeInQuad, 0.1, 1.0)

    @JvmStatic val bounce3: Ease = CompoundEase()
            .addEase(easeInQuad, 1.0, 1.0)
            .addEase(easeOutQuad, 0.2, 0.8)
            .addEase(easeInQuad, 0.2, 1.0)
            .addEase(easeOutQuad, 0.1, 0.95)
            .addEase(easeInQuad, 0.1, 1.0)
            .addEase(easeOutQuad, 0.05, 0.99)
            .addEase(easeInQuad, 0.05, 1.0)

}


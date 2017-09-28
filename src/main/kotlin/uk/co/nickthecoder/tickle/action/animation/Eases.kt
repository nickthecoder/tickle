package uk.co.nickthecoder.tickle.action.animation

object Eases {

    val linear: Ease = LinearEase.instance
    val defaultEase: Ease = BezierEase(0.250f, 0.100f, 0.250f, 1.000f)

    val easeIn: Ease = BezierEase(0.420f, 0.000f, 1.000f, 1.000f)
    val easeInQuad: Ease = BezierEase(0.550f, 0.085f, 0.680f, 0.530f)
    val easeInCubic: Ease = BezierEase(0.895f, 0.030f, 0.685f, 0.220f)
    val easeInExpo: Ease = BezierEase(0.950f, 0.050f, 0.795f, 0.035f)
    val easeInCirc: Ease = BezierEase(0.600f, 0.040f, 0.980f, 0.335f)
    val easeInBack: Ease = BezierEase(0.610f, -0.255f, 0.730f, 0.015f)

    val easeOut: Ease = BezierEase(0.000f, 0.000f, 0.580f, 1.000f)
    val easeOutQuad: Ease = BezierEase(0.250f, 0.460f, 0.450f, 0.940f)
    val easeOutCubic: Ease = BezierEase(0.215f, 0.610f, 0.355f, 1.000f)
    val easeOutExpo: Ease = BezierEase(0.190f, 1.000f, 0.220f, 1.000f)
    val easeOutCirc: Ease = BezierEase(0.075f, 0.820f, 0.165f, 1.000f)
    val easeOutBack: Ease = BezierEase(0.175f, 0.885f, 0.320f, 1.275f)

    val easeInOut: Ease = BezierEase(0.420f, 0.000f, 0.580f, 1.000f)
    val easeInOutQuad: Ease = BezierEase(0.455f, 0.030f, 0.515f, 0.955f)
    val easeInOutCubic: Ease = BezierEase(0.645f, 0.045f, 0.355f, 1.000f)
    val easeInOutExpo: Ease = BezierEase(1.000f, 0.000f, 0.000f, 1.000f)
    val easeInOutCirc: Ease = BezierEase(0.785f, 0.135f, 0.150f, 0.860f)
    val easeInOutBack: Ease = BezierEase(0.680f, -0.550f, 0.265f, 1.550f)


    val bounce: Ease = CompoundEase()
            .addEase(easeInQuad, 1f, 1f)
            .addEase(easeOutQuad, 0.2f, 0.8f)
            .addEase(easeInQuad, 0.2f, 1f)

    val bounce2: Ease = CompoundEase()
            .addEase(easeInQuad, 1f, 1f)
            .addEase(easeOutQuad, 0.2f, 0.8f)
            .addEase(easeInQuad, 0.2f, 1f)
            .addEase(easeOutQuad, 0.1f, 0.95f)
            .addEase(easeInQuad, 0.1f, 1f)

    val bounce3: Ease = CompoundEase()
            .addEase(easeInQuad, 1f, 1f)
            .addEase(easeOutQuad, 0.2f, 0.8f)
            .addEase(easeInQuad, 0.2f, 1f)
            .addEase(easeOutQuad, 0.1f, 0.95f)
            .addEase(easeInQuad, 0.1f, 1f)
            .addEase(easeOutQuad, 0.05f, 0.99f)
            .addEase(easeInQuad, 0.05f, 1f)

}


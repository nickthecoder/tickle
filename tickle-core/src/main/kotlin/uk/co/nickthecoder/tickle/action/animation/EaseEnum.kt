package uk.co.nickthecoder.tickle.action.animation

/**
 * This contains the same [Ease] constants as the object [Eases], but provides them in an enum,
 * so that they can be used within as an @Attribute (and therefore can be picked from
 * Tickle's resources editor / scene editor).
 */
enum class EaseEnum : Ease {

    linear {
        override fun ease(t: Double) = LinearEase.instance.ease(t)
    },


    easeIn {
        override fun ease(t: Double) = Eases.easeIn.ease(t)
    },
    easeInQuad {
        override fun ease(t: Double) = Eases.easeInQuad.ease(t)
    },
    easeInCubic {
        override fun ease(t: Double) = Eases.easeInCubic.ease(t)
    },
    easeInExpo {
        override fun ease(t: Double) = Eases.easeInExpo.ease(t)
    },
    easeInCirc {
        override fun ease(t: Double) = Eases.easeInCirc.ease(t)
    },
    easeInBack {
        override fun ease(t: Double) = Eases.easeInBack.ease(t)
    },


    easeOut {
        override fun ease(t: Double) = Eases.easeOut.ease(t)
    },
    easeOutQuad {
        override fun ease(t: Double) = Eases.easeOutQuad.ease(t)
    },
    easeOutCubic {
        override fun ease(t: Double) = Eases.easeOutCubic.ease(t)
    },
    easeOutExpo {
        override fun ease(t: Double) = Eases.easeOutExpo.ease(t)
    },
    easeOutCirc {
        override fun ease(t: Double) = Eases.easeOutCirc.ease(t)
    },
    easeOutBack {
        override fun ease(t: Double) = Eases.easeOutBack.ease(t)
    },


    easeInOut {
        override fun ease(t: Double) = Eases.easeInOut.ease(t)
    },
    easeInOutQuad {
        override fun ease(t: Double) = Eases.easeInOutQuad.ease(t)
    },
    easeInOutCubic {
        override fun ease(t: Double) = Eases.easeInOutCubic.ease(t)
    },
    easeInOutExpo {
        override fun ease(t: Double) = Eases.easeInOutExpo.ease(t)
    },
    easeInOutCirc {
        override fun ease(t: Double) = Eases.easeInOutCirc.ease(t)
    },
    easeInOutBack {
        override fun ease(t: Double) = Eases.easeInOutBack.ease(t)
    },


    bounce {
        override fun ease(t: Double) = Eases.bounce.ease(t)
    },
    bounce2 {
        override fun ease(t: Double) = Eases.bounce2.ease(t)
    },
    bounce3 {
        override fun ease(t: Double) = Eases.bounce3.ease(t)
    }

}

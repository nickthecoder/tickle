package uk.co.nickthecoder.tickle.action.animation

import java.util.*


/**
 * Join three eases together into a single compound ease. Imagine an Ease as a graph which goes from (0,0) to (1,1), a linear Ease would be
 * a straight line, and a EaseInOut Ease would be a slanted S shape. A CompoundEase is a set of these graphs all joined together. Each
 * section is an ease which goes from 0 to 1, but the CompoundEase, can change the destination for all but the last Ease. For example, if we
 * joined two LinearEases together, which the first destination was 0.5, then it would be identical to a single LinearEase. However, if we
 * made the first destination 0.1, then the first part of the animation would change slowly (as the gradient is shallow), and the second
 * half would be quick (going from 0.1 to 1 in the same time as the first half took to go from 0 to 0.1).
 */
class CompoundEase : Ease {

    private val sections: MutableList<Section>

    private var totalWidth: Float = 0.toFloat()

    init {
        this.sections = ArrayList<Section>()
        this.totalWidth = 0f
    }

    /**
     * Note, the last ease to be added should always have a destination of 1.

     * @param ease
     * *
     * @param width
     * *        The amount of time that this ease is to be used relative to the other added eases
     * *
     * @param destination
     */
    fun addEase(ease: Ease, width: Float, destination: Float): CompoundEase {
        this.totalWidth += width
        val prevY: Float
        if (this.sections.isEmpty()) {
            prevY = 0f
        } else {
            val prevSection = this.sections[this.sections.size - 1]
            prevY = prevSection.y0 + prevSection.actualHeight
        }
        this.sections.add(Section(ease, width, prevY, destination))

        var accumulatedWidth = 0f
        sections.forEach { section ->
            section.actualWidth = section.width / this.totalWidth
            section.x0 = accumulatedWidth
            accumulatedWidth += section.actualWidth
        }
        return this
    }

    override fun ease(t: Float): Float {
        for (section in this.sections) {
            if (section.x0 + section.actualWidth >= t) {
                return section.amount(t)
            }
        }
        return 1f
    }

    override fun toString(): String {
        val buffer = StringBuffer()

        buffer.append("CompoundEase : \n")
        for (section in this.sections) {
            buffer.append(section.ease)
                    .append(" x0 : ").append(section.x0)
                    .append(" width : ").append(section.actualWidth)
                    .append(" y0 : ").append(section.y0)
                    .append(" height : ").append(section.actualHeight)
                    .append("\n")
        }
        return buffer.toString()
    }

    private inner class Section(internal var ease: Ease, internal var width: Float, internal var y0: Float, y1: Float) {

        internal var actualWidth: Float = 0.toFloat()
        internal var actualHeight: Float = 0.toFloat()

        internal var x0: Float = 0.toFloat()

        init {
            this.actualHeight = y1 - y0
            // x0 and actualWidth are calculated by parent class every time a section is added.
        }

        fun amount(t: Float): Float {
            val amount = (t - this.x0) / this.actualWidth

            val result = this.ease.ease(amount)

            return result * this.actualHeight + this.y0
        }
    }
}

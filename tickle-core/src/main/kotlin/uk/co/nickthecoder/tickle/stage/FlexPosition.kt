package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.util.Recti

class FlexPosition {

    var hAlignment: FlexHAlignment = FlexHAlignment.LEFT
    var hPosition: Double = 0.5
    var leftRightMargin: Int = 0
    var width: Int? = null
    var widthRatio: Double? = null

    var vAlignment: FlexVAlignment = FlexVAlignment.TOP
    var vPosition: Double = 0.5
    var topBottomMargin: Int = 0
    var height: Int? = null
    var heightRatio: Double? = null

    fun width(totalWidth: Int): Int {
        return width ?: widthRatio?.let {
            (totalWidth / it).toInt()
        } ?: totalWidth - if (hAlignment == FlexHAlignment.MIDDLE) 0 else leftRightMargin
    }

    fun left(totalWidth: Int): Int {

        return when (hAlignment) {
            FlexHAlignment.LEFT -> leftRightMargin
            FlexHAlignment.RIGHT -> right(totalWidth) - width(totalWidth)
            FlexHAlignment.MIDDLE -> ((hPosition * totalWidth - width(totalWidth) / 2)).toInt()
        }
    }

    fun right(totalWidth: Int): Int {

        return when (hAlignment) {
            FlexHAlignment.LEFT -> left(totalWidth) + width(totalWidth)
            FlexHAlignment.RIGHT -> totalWidth - leftRightMargin
            FlexHAlignment.MIDDLE -> ((hPosition * totalWidth + width(totalWidth) / 2)).toInt()
        }
    }


    fun height(totalHeight: Int): Int {
        return height ?: heightRatio?.let {
            (totalHeight / it).toInt()
        } ?: totalHeight - if (vAlignment == FlexVAlignment.MIDDLE) 0 else topBottomMargin
    }

    fun bottom(totalHeight: Int): Int {
        return when (vAlignment) {
            FlexVAlignment.BOTTOM -> topBottomMargin
            FlexVAlignment.TOP -> top(totalHeight) - height(totalHeight)
            FlexVAlignment.MIDDLE -> ((vPosition * totalHeight - height(totalHeight) / 2)).toInt()
        }
    }

    fun top(totalHeight: Int): Int {

        return when (vAlignment) {
            FlexVAlignment.BOTTOM -> bottom(totalHeight) + height(totalHeight)
            FlexVAlignment.TOP -> totalHeight - topBottomMargin
            FlexVAlignment.MIDDLE -> ((vPosition * totalHeight + height(totalHeight) / 2)).toInt()
        }
    }

    fun calculateRectangle(totalWidth: Int, totalHeight: Int) = Recti(left(totalWidth), bottom(totalHeight), right(totalWidth), top(totalHeight))

    override fun toString(): String {
        return "hAlign=$hAlignment hPosition=$hPosition leftRightMargin=$leftRightMargin width=$width widthRatio=$widthRatio\n" +
                " valign=$vAlignment vPosition=$vPosition topBottomMargin=$topBottomMargin height=$height heightRation=$heightRatio"
    }
}

enum class FlexHAlignment {
    LEFT, MIDDLE, RIGHT
}

enum class FlexVAlignment {
    TOP, MIDDLE, BOTTOM
}

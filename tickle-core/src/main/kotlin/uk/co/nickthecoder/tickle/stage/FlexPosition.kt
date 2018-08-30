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

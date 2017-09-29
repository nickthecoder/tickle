package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.stage.ZOrderStageView
import uk.co.nickthecoder.tickle.util.Recti

class Layout() {

    val views = mutableMapOf<String, LayoutView>()

}

class LayoutView() {

    var viewString: String = ZOrderStageView::class.java.name

    var leftAligned: Boolean = true
    var leftRightMargin: Int = 0
    var width: Int? = null
    var widthRatio: Float? = 1f

    var bottomAligned: Boolean = true
    var topBottomMargin: Int = 0
    var height: Int? = null
    var heightRatio: Float? = 1f

    fun left(totalWidth: Int): Int = if (leftAligned) leftRightMargin else right(totalWidth) - width(totalWidth)
    fun right(totalWidth: Int): Int = if (!leftAligned) totalWidth - leftRightMargin else totalWidth - left(totalWidth)
    fun width(totalWidth: Int): Int = width ?: widthRatio?.let { (totalWidth / it).toInt() } ?: totalWidth - leftRightMargin

    fun bottom(totalHeight: Int): Int = if (bottomAligned) topBottomMargin else top(totalHeight) - height(totalHeight)
    fun top(totalHeight: Int): Int = if (!bottomAligned) totalHeight - topBottomMargin else totalHeight - bottom(totalHeight)
    fun height(totalHeight: Int): Int = height ?: heightRatio?.let { (totalHeight / it).toInt() } ?: totalHeight - topBottomMargin

    fun rect(totalWidth: Int, totalHeight: Int) = Recti(left(totalWidth), bottom(totalHeight), right(totalWidth), top(totalHeight))

}

package uk.co.nickthecoder.tickle.stage

data class Rectangle(
        val left: Int,
        val bottom: Int,
        val right: Int,
        val top: Int) {

    val width = right - left
    val height = top - bottom
}

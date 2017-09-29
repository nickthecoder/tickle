package uk.co.nickthecoder.tickle

class GameInfo(
        var title: String,
        var width: Int,
        var height: Int,
        var resizable: Boolean,
        var gameClassString: String = DefaultGame::class.java.name) {
    init {
        println("GameInfo game class = $gameClassString")
    }
}
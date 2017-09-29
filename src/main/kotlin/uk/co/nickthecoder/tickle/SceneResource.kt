package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Color
import java.io.File

/**
 * Used when loading and editing a Scene. Not used during actual game play.
 */
class SceneResource {

    val file: File? = null

    var name: String = ""

    var background: Color = Color.BLACK

    /**
     * Keyed on the name of the stage
     */
    val layers = mutableMapOf<String, Positions>()
}

/**
 * Details of all the Actors' initial state
 */
class Positions {

    val positions = mutableListOf<Position>()

}

class Position(
        var costumeName: String,
        var x: Float,
        var y: Float,
        var direction: Double)



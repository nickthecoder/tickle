package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2f

interface StageView : View {

    var stage: Stage

    var centerX : Float

    var centerY : Float

    fun mousePosition(): Vector2f

}

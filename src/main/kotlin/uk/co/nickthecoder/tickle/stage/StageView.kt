package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2d

interface StageView : View {

    var stage: Stage

    var centerX : Double

    var centerY : Double

    fun mousePosition(): Vector2d

}

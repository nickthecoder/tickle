package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Texture

// TODO Will have a clipping rectangle within the texture
class Pose(val name: String, val texture: Texture) {

    var offsetX: Float = 0f
    var offsetY: Float = 0f

    fun draw(renderer: Renderer, actor: Actor) {
        renderer.drawTexture(texture, actor.x - offsetX, actor.y - offsetY, color = actor.color)
    }

}

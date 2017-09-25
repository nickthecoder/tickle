package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

/**
 * This is currently a placeholder class, containing a coupld of textures, but eventually, it will contain
 * information about all of the resources used by a game, and the meta data will be loaded from a file.
 */
class Resources {

    var coin: Texture = Texture.createTexture(File(Game.resourceDirectory, "coin.png"))
    var grenade = Texture.createTexture(File(Game.resourceDirectory, "grenade.png"))

}

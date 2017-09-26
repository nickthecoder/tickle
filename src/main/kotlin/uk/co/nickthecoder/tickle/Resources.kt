package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

/**
 * This is currently a placeholder class, containing a coupld of textures, but eventually, it will contain
 * information about all of the resources used by a game, and the meta data will be loaded from a file.
 */
class Resources {

    val beeTexture: Texture = Texture.createTexture(File(Game.resourceDirectory, "bee.png"))
    val coinTexture: Texture = Texture.createTexture(File(Game.resourceDirectory, "coin.png"))
    val grenadeTexture = Texture.createTexture(File(Game.resourceDirectory, "grenade.png"))

    val beePose = Pose("bee", beeTexture)
    val coinPose = Pose("bee", coinTexture)
    val grenadePose = Pose("grenade", grenadeTexture)

    init {
        beePose.offsetX = 30f
        beePose.offsetY = 30f
    }
}

package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.events.KeyInput
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

/**
 * This is currently a placeholder class, containing a coupld of textures, but eventually, it will contain
 * information about all of the resources used by a game, and the meta data will be loaded from a file.
 */
class Resources {

    val imageDir = File(Game.resourceDirectory, "images")

    val beeTexture: Texture = Texture.createTexture(File(imageDir, "bee.png"))
    val handTexture: Texture = Texture.createTexture(File(imageDir, "hand.png"))
    val coinTexture: Texture = Texture.createTexture(File(imageDir, "coin.png"))
    val grenadeTexture = Texture.createTexture(File(imageDir, "grenade.png"))
    val sparkTexture = Texture.createTexture(File(imageDir, "spark.png"))

    val beePose = Pose("bee", beeTexture)
    val handPose = Pose("hand", handTexture)
    val coinPose = Pose("bee", coinTexture)
    val grenadePose = Pose("grenade", grenadeTexture)
    val sparkPose = Pose("spark", sparkTexture)

    val inputs = mutableMapOf<String, Input>()

    val dummyInput = CompoundInput()

    init {
        beePose.offsetX = 30f
        beePose.offsetY = 30f

        handPose.offsetX = 17f
        handPose.offsetY = 27f

        coinPose.offsetX = 30f
        coinPose.offsetY = 30f

        inputs.put("left", KeyInput(GLFW.GLFW_KEY_LEFT))
        inputs.put("right", KeyInput(GLFW.GLFW_KEY_RIGHT))
        inputs.put("up", KeyInput(GLFW.GLFW_KEY_UP))
        inputs.put("down", KeyInput(GLFW.GLFW_KEY_DOWN))
        inputs.put("reset", KeyInput(GLFW.GLFW_KEY_O))
        inputs.put("clockwise", KeyInput(GLFW.GLFW_KEY_X))
        inputs.put("anti-clockwise", KeyInput(GLFW.GLFW_KEY_Z))
        inputs.put("toggle", KeyInput(GLFW.GLFW_KEY_TAB))
    }

    fun input(name: String): Input {
        val input = inputs[name]
        if (input == null) {
            System.err.println("Warning. Input $name not found.")
            return dummyInput
        } else {
            return input
        }
    }

    companion object {
        /**
         * A convience, so that game scripts can easily get access to the resources.
         */
        var instance = Resources()
    }
}

package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.events.KeyInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Recti
import java.io.File

/**
 * This is currently a placeholder class, containing a coupld of textures, but eventually, it will contain
 * information about all of the resources used by a game, and the meta data will be loaded from a file.
 */
class Resources {

    val imageDir = File(Game.resourceDirectory, "images")

    val spritesTexture: Texture = Texture.createTexture(File(imageDir, "sprites.png"))

    val grenadePose = Pose("grenade", spritesTexture, Recti(0, 199, 44, 139))
    val beePose = Pose("bee", spritesTexture, Recti(0, 60, 75, 0))
    val handPose = Pose("hand", spritesTexture, Recti(135, 60, 185, 0))
    val coinPose = Pose("coin", spritesTexture, Recti(75, 60, 135, 0))
    val sparkPose = Pose("spark", spritesTexture, Recti(0, 100, 40, 60))

    val inputs = mutableMapOf<String, Input>()

    val dummyInput = CompoundInput()

    init {

        sparkPose.offsetX = 20f
        sparkPose.offsetY = 20f

        grenadePose.offsetX = 20f
        grenadePose.offsetY = 30f

        beePose.offsetX = 38f
        beePose.offsetY = 29f
        beePose.directionRadians= Math.toRadians(24.0)

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

    fun optionalInput(name: String?): Input {
        name?.let {
            return input(it)
        }
        return dummyInput
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

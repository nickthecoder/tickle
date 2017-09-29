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

    var file: File = File("")

    val resourceDirectory: File
        get() = file.parentFile ?: File(".").absoluteFile

    // TODO Remove this when loading is written.
    private val imageDir
        get() = File(Game.resourceDirectory, "images")


    val gameInfo = GameInfo("Tickle", 600, 400, true)

    private val textures = mutableMapOf<String, TextureResource>()

    private val poses = mutableMapOf<String, Pose>()

    private val inputs = mutableMapOf<String, Input>()

    private val dummyInput = CompoundInput()

    init {

        val tr = TextureResource(File(imageDir, "sprites.png"))
        textures["sprites"] = tr
        val spritesTexture = tr.texture

        val grenadePose = Pose(spritesTexture, Recti(0, 199, 44, 139))
        val beePose = Pose(spritesTexture, Recti(0, 60, 75, 0))
        val handPose = Pose(spritesTexture, Recti(135, 60, 185, 0))
        val coinPose = Pose(spritesTexture, Recti(75, 60, 135, 0))
        val sparkPose = Pose(spritesTexture, Recti(0, 100, 40, 60))

        sparkPose.offsetX = 20f
        sparkPose.offsetY = 20f

        grenadePose.offsetX = 20f
        grenadePose.offsetY = 30f

        beePose.offsetX = 38f
        beePose.offsetY = 29f
        beePose.directionRadians = Math.toRadians(24.0)

        handPose.offsetX = 17f
        handPose.offsetY = 27f

        coinPose.offsetX = 30f
        coinPose.offsetY = 30f

        poses["bee"] = beePose
        poses["grenade"] = grenadePose
        poses["hand"] = handPose
        poses["coin"] = coinPose
        poses["spark"] = sparkPose


        inputs.put("left", KeyInput(GLFW.GLFW_KEY_LEFT))
        inputs.put("right", KeyInput(GLFW.GLFW_KEY_RIGHT))
        inputs.put("up", KeyInput(GLFW.GLFW_KEY_UP))
        inputs.put("down", KeyInput(GLFW.GLFW_KEY_DOWN))
        inputs.put("reset", KeyInput(GLFW.GLFW_KEY_O))
        inputs.put("clockwise", KeyInput(GLFW.GLFW_KEY_X))
        inputs.put("anti-clockwise", KeyInput(GLFW.GLFW_KEY_Z))
        inputs.put("toggle", KeyInput(GLFW.GLFW_KEY_TAB))
    }

    fun textures(): Map<String, TextureResource> = textures

    fun optionalTexture(name: String): Texture? {
        return textures[name]?.texture
    }

    fun textureResource(name: String): TextureResource {
        return textures[name] ?: throw IllegalStateException("Texture $name not found")
    }

    fun texture(name: String): Texture {
        return textureResource(name).texture
    }

    fun findTextureResource(texture: Texture): TextureResource? {
        return textures.values.firstOrNull { it.texture === texture }
    }

    fun findTextureName(texture: Texture): String? {
        return textures.filter { entry -> entry.value.texture === texture }.map { it.key }.firstOrNull()
    }


    fun poses(): Map<String, Pose> = poses

    fun optionalPose(name: String): Pose? {
        return poses[name]
    }

    fun pose(name: String): Pose {
        return poses[name] ?: throw IllegalStateException("Pose $name not found")
    }


    fun inputs(): Map<String, Input> = inputs

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

class TextureResource(val file: File) {

    val texture = Texture.createTexture(file)
}

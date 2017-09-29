package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

/**
 * This is currently a placeholder class, containing a coupld of textures, but eventually, it will contain
 * information about all of the resources used by a game, and the meta data will be loaded from a file.
 */
class Resources {

    var file: File = File("")

    val resourceDirectory: File
        get() = file.parentFile ?: File(".").absoluteFile
    

    val gameInfo = GameInfo("Tickle", 600, 400, true)

    private val textures = mutableMapOf<String, TextureResource>()

    private val poses = mutableMapOf<String, Pose>()

    private val inputs = mutableMapOf<String, Input>()

    private val dummyInput = CompoundInput()


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

    fun addTexture(name: String, file: File) {
        textures[name] = TextureResource(file)
    }


    fun poses(): Map<String, Pose> = poses

    fun optionalPose(name: String): Pose? {
        return poses[name]
    }

    fun pose(name: String): Pose {
        return poses[name] ?: throw IllegalStateException("Pose $name not found")
    }

    fun addPose(name: String, pose: Pose) {
        poses[name] = pose
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

    fun addInput(name: String, input: Input) {
        inputs[name] = input
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

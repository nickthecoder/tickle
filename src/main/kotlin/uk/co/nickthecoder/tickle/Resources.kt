package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

class Resources {

    var file: File = File("")

    val resourceDirectory: File
        get() = file.parentFile ?: File(".").absoluteFile


    val gameInfo = GameInfo("Tickle", 600, 400, true)

    private val textures = mutableMapOf<String, TextureResource>()

    private val poses = mutableMapOf<String, Pose>()

    private val costumes = mutableMapOf<String, Costume>()

    private val inputs = mutableMapOf<String, Input>()

    private val layouts = mutableMapOf<String, Layout>()

    private val dummyInput = CompoundInput()


    val sceneDirectory: File
        get() = File(file.parentFile, "scenes")

    val texturesDirectory: File
        get() = File(file.parentFile, "textures")


    val listeners = mutableListOf<ResourcesListener>()


    private fun fireAdded(resource: Any, name: String) {
        listeners.forEach {
            it.added(resource, name)
        }
    }

    private fun fireRemoved(resource: Any, name: String) {
        listeners.forEach {
            it.removed(resource, name)
        }
    }

    private fun fireRenamed(resource: Any, oldName: String, newName: String) {
        listeners.forEach {
            it.renamed(resource, oldName, newName)
        }
    }

    fun fireChanged(resource: Any) {
        listeners.forEach {
            it.changed(resource)
        }
    }

    // TEXTURES

    fun textures(): Map<String, TextureResource> = textures

    fun optionalTextureResource(name: String): TextureResource? {
        return textures[name]
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
        val textureResource = TextureResource(file)
        textures[name] = textureResource
        fireAdded(textureResource, name)
    }

    fun renameTexture(oldName: String, newName: String) {
        textures[oldName]?.let { textureResource ->
            textures.remove(oldName)
            textures[newName] = textureResource
            fireRenamed(textureResource, oldName, newName)
        }
    }

    // POSES

    fun poses(): Map<String, Pose> = poses

    fun optionalPose(name: String): Pose? {
        return poses[name]
    }

    fun pose(name: String): Pose {
        return poses[name] ?: throw IllegalStateException("Pose $name not found")
    }

    fun findPoseName(pose: Pose): String? {
        return poses.filter { entry -> entry.value === pose }.map { it.key }.firstOrNull()
    }

    fun addPose(name: String, pose: Pose) {
        poses[name] = pose
        fireAdded(pose, name)
    }

    fun renamePose(oldName: String, newName: String) {
        poses[oldName]?.let { pose ->
            poses.remove(oldName)
            poses[newName] = pose
            fireRenamed(pose, oldName, newName)
        }
    }

    // COSTUMES

    fun costumes(): Map<String, Costume> = costumes

    fun optionalCostume(name: String) = costumes[name]

    fun costume(name: String): Costume {
        return costumes[name] ?: throw IllegalArgumentException("Costume $name not found")
    }

    fun addCostume(name: String, costume: Costume) {
        costumes[name] = costume
        fireAdded(costume, name)
    }

    fun renameCostume(oldName: String, newName: String) {
        costumes[oldName]?.let { costume ->
            costumes.remove(oldName)
            costumes[newName] = costume
            fireRenamed(costume, oldName, newName)
        }
    }

    // INPUTS

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
        fireAdded(input, name)
    }

    fun renameInput(oldName: String, newName: String) {
        inputs[oldName]?.let { input ->
            inputs.remove(oldName)
            inputs[newName] = input
            fireRenamed(input, oldName, newName)
        }
    }

    // LAYOUT

    fun layouts(): Map<String, Layout> = layouts

    fun layout(name: String): Layout {
        val layout = layouts[name]
        if (layout == null)
            if (name == "default") {
                throw IllegalArgumentException("Couldn't find layout '$name'")
            } else {
                System.err.println("ERROR. Couldn't find layout '$name'. Attempting to use 'default' instead.")
                return layout("default")
            }
        return layout
    }

    fun addLayout(name: String, layout: Layout) {
        layouts[name] = layout
        fireAdded(layout, name)
    }

    fun renameLayout(oldName: String, newName: String) {
        layouts[oldName]?.let { layout ->
            layouts.remove(oldName)
            layouts[newName] = layout
            fireRenamed(layout, oldName, newName)
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

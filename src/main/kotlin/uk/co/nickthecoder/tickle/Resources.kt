package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File

class Resources {

    var file: File = File("")

    val resourceDirectory: File
        get() = file.parentFile ?: File(".").absoluteFile


    val gameInfo = GameInfo("Tickle", 600, 400, true)

    private val textures = mutableMapOf<String, TextureResource>()

    private val poses = mutableMapOf<String, Pose>()

    private val costumes = mutableMapOf<String, Costume>()

    private val inputs = mutableMapOf<String, CompoundInput>()

    private val layouts = mutableMapOf<String, Layout>()


    val sceneDirectory: File
        get() = File(file.parentFile, "scenes")

    val texturesDirectory: File
        get() = File(file.parentFile, "textures")


    val listeners = mutableListOf<ResourcesListener>()


    private fun fireAdded(resource: Any, name: String) {
        listeners.toList().forEach {
            it.resourceAdded(resource, name)
        }
    }

    private fun fireRemoved(resource: Any, name: String) {
        listeners.toList().forEach {
            it.resourceRemoved(resource, name)
        }
    }

    private fun fireRenamed(resource: Any, oldName: String, newName: String) {
        listeners.toList().forEach {
            it.resourceRenamed(resource, oldName, newName)
        }
    }

    fun fireChanged(resource: Any) {
        listeners.toList().forEach {
            it.resourceChanged(resource)
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

    fun deleteTexture(name : String) {
        textures[name]?.let {
            textures.remove(name)
            fireRemoved(it, name)
        }
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

    fun findPoseName(pose: Pose?): String? {
        return poses.filter { entry -> entry.value === pose }.map { it.key }.firstOrNull()
    }

    fun addPose(name: String, pose: Pose) {
        poses[name] = pose
        fireAdded(pose, name)
    }

    fun deletePose(name : String) {
        poses[name]?.let {
            poses.remove(name)
            fireRemoved(it, name)
        }
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

    fun deleteCostume(name : String) {
        costumes[name]?.let {
            costumes.remove(name)
            fireRemoved(it, name)
        }
    }

    fun renameCostume(oldName: String, newName: String) {
        costumes[oldName]?.let { costume ->
            costumes.remove(oldName)
            costumes[newName] = costume
            fireRenamed(costume, oldName, newName)
        }
    }

    // INPUTS

    fun inputs(): Map<String, CompoundInput> = inputs

    fun optionalInput(name: String?) = inputs[name]

    fun input(name: String): CompoundInput {
        val input = inputs[name]
        if (input == null) {
            System.err.println("Warning. Input $name not found.")
            return Input.dummyInput
        } else {
            return input
        }
    }

    fun addInput(name: String, input: CompoundInput) {
        inputs[name] = input
        fireAdded(input, name)
    }

    fun deleteInput(name : String) {
        inputs[name]?.let {
            inputs.remove(name)
            fireRemoved(it, name)
        }
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

    fun optionalLayout(name: String): Layout? = layouts[name]

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

    fun deleteLayout(name : String) {
        layouts[name]?.let {
            layouts.remove(name)
            fireRemoved(it, name)
        }
    }

    fun renameLayout(oldName: String, newName: String) {
        layouts[oldName]?.let { layout ->
            layouts.remove(oldName)
            layouts[newName] = layout
            fireRenamed(layout, oldName, newName)
        }
    }

    fun save() {
        JsonResources(this).save(this.file)
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

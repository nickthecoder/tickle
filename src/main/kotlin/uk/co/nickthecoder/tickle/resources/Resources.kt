package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.util.ResourceType
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.sound.Sound
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File

class Resources {

    var file: File = File("")

    val resourceDirectory: File
        get() = file.parentFile ?: File(".").absoluteFile


    val gameInfo = GameInfo("Tickle", "ticklegame", 600, 400, fullScreen = false, resizable = false)

    val preferences = EditorPreferences()

    val textures = ResourceMap<Texture>(this, ResourceType.TEXTURE)

    val poses = ResourceMap<Pose>(this, ResourceType.POSE)

    val costumes = ResourceMap<Costume>(this, ResourceType.COSTUME)

    val costumeGroups = ResourceMap<CostumeGroup>(this, ResourceType.COSTUME_GROUP)

    val inputs = ResourceMap<CompoundInput>(this, ResourceType.INPUT)

    val layouts = ResourceMap<Layout>(this, ResourceType.LAYOUT)

    val fontResources = ResourceMap<FontResource>(this, ResourceType.FONT)

    val sounds = ResourceMap<Sound>(this, ResourceType.SOUND)


    val sceneDirectory: File
        get() = File(file.parentFile, "scenes").absoluteFile

    val texturesDirectory: File
        get() = File(file.parentFile, "images").absoluteFile

    val listeners = mutableListOf<ResourcesListener>()

    fun findName(resource: Any): String? {
        return when (resource) {
            is Texture -> textures.findName(resource)
            is Pose -> poses.findName(resource)
            is Costume -> costumes.findName(resource)
            is CostumeGroup -> costumeGroups.findName(resource)
            is CompoundInput -> inputs.findName(resource)
            is Layout -> layouts.findName(resource)
            is FontResource -> fontResources.findName(resource)
            else -> null
        }
    }

    fun findCostumeGroup(costumeName: String): CostumeGroup? {
        costumeGroups.items().values.forEach { costumeGroup ->
            if (costumeGroup.find(costumeName) != null) {
                return costumeGroup
            }
        }
        return null
    }

    fun toPath(file: File): String {
        try {
            return file.absoluteFile.toRelativeString(resourceDirectory)
        } catch(e: Exception) {
            return file.absolutePath
        }
    }

    fun fromPath(path: String): File {
        return resourceDirectory.resolve(path).absoluteFile
    }

    fun scenePathToFile(path: String): File {
        return sceneDirectory.resolve(path + ".scene")
    }

    fun sceneFileToPath(file: File): String {
        val path = if (file.isAbsolute) {
            file.relativeToOrSelf(sceneDirectory).path
        } else {
            file.path
        }
        if (path.endsWith(".scene")) {
            return path.substring(0, path.length - 6)
        } else {
            return path
        }
    }

    fun fireAdded(resource: Any, name: String) {
        listeners.toList().forEach {
            it.resourceAdded(resource, name)
        }
    }

    fun fireRemoved(resource: Any, name: String) {
        listeners.toList().forEach {
            it.resourceRemoved(resource, name)
        }
    }

    fun fireRenamed(resource: Any, oldName: String, newName: String) {
        listeners.toList().forEach {
            it.resourceRenamed(resource, oldName, newName)
        }
    }

    fun fireChanged(resource: Any) {
        listeners.toList().forEach {
            it.resourceChanged(resource)
        }
    }

    fun save() {
        JsonResources(this).save(this.file)
    }

    companion object {
        /**
         * A convenience, so that game scripts can easily get access to the resources.
         */
        lateinit var instance: Resources
    }
}

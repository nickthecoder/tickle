/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.sound.Sound
import java.io.File
import java.lang.IllegalStateException

open class Resources {

    var file: File = File("")

    val resourceDirectory: File
        get() = file.parentFile ?: File(".").absoluteFile


    val gameInfo = GameInfo("Tickle", "ticklegame", 600, 400, fullScreen = false, resizable = false)

    val preferences = EditorPreferences()

    val textures = ResourceMap<Texture>(this)

    val poses = ResourceMap<Pose>(this)

    val costumes = ResourceMap<Costume>(this)

    val costumeGroups = ResourceMap<CostumeGroup>(this)

    val inputs = ResourceMap<CompoundInput>(this)

    val layouts = ResourceMap<Layout>(this)

    val fontResources = ResourceMap<FontResource>(this)

    val sounds = ResourceMap<Sound>(this)


    val sceneDirectory: File
        get() = File(file.parentFile, "scenes").absoluteFile

    val texturesDirectory: File
        get() = File(file.parentFile, "images").absoluteFile

    val listeners = mutableListOf<ResourcesListener>()

    init {
        instance = this
    }

    open fun save() {
        throw IllegalStateException("Resources are read-only")
    }

    open fun createAttributes(): Attributes {
        return RuntimeAttributes()
    }

    fun findName(resource: Any): String? {
        return when (resource) {
            is Texture -> textures.findName(resource)
            is Pose -> poses.findName(resource)
            is Costume -> costumes.findName(resource)
            is CostumeGroup -> costumeGroups.findName(resource)
            is CompoundInput -> inputs.findName(resource)
            is Layout -> layouts.findName(resource)
            is FontResource -> fontResources.findName(resource)
            is SceneStub -> resource.name
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

    /**
     * Reloads the textures, sounds and fonts.
     */
    fun reload() {
        textures.items().values.forEach { it.reload() }
        sounds.items().values.forEach { it.reload() }
        fontResources.items().values.forEach { it.reload() }
    }

    fun scriptDirectory() = File(file.parent, "scripts")

    fun destroy() {
        textures.items().values.forEach { it.destroy() }
        fontResources.items().values.forEach { it.destroy() }
        sounds.items().values.forEach { it.destroy() }
    }

    companion object {
        /**
         * A convenience, so that game scripts can easily get access to the resources.
         */
        lateinit var instance: Resources
    }
}

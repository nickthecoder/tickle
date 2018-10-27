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
package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.physics.TickleBodyDef
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.SceneStub
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import uk.co.nickthecoder.tickle.sound.Sound
import uk.co.nickthecoder.tickle.util.Copyable
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Dependable
import uk.co.nickthecoder.tickle.util.Renamable

class Costume : Copyable<Costume>, Deletable, Renamable, Dependable {

    var roleString: String = ""
        set(v) {
            if (field != v) {
                field = v
                attributes.updateAttributesMetaData(v)
            }
        }

    var canRotate: Boolean = false

    var canScale: Boolean = false

    var zOrder: Double = 0.0

    val attributes = Resources.instance.createAttributes()

    val events = mutableMapOf<String, CostumeEvent>()

    var costumeGroup: CostumeGroup? = null

    var initialEventName: String = "default"

    var showInSceneEditor: Boolean = true

    var inheritEventsFrom: Costume? = null

    var bodyDef: TickleBodyDef? = null

    fun createActor(text: String = ""): Actor {
        val role = if (roleString.isBlank()) null else Role.create(roleString)
        role?.let { attributes.applyToObject(it) }

        val actor = Actor(this, role)
        actor.zOrder = zOrder

        val ninePatch = chooseNinePatch(initialEventName)
        if (ninePatch == null) {
            val pose = choosePose(initialEventName)
            if (pose == null) {
                val textStyle = chooseTextStyle(initialEventName)
                if (textStyle != null) {
                    actor.changeAppearance(text, textStyle.copy())
                }
            } else {
                actor.changeAppearance(pose)
            }
        } else {
            val ninePatchAppearance = NinePatchAppearance(actor, ninePatch)
            // TODO What should the size be?
            ninePatchAppearance.resize(ninePatch.pose.rect.width.toDouble(), ninePatch.pose.rect.height.toDouble())
            actor.appearance = ninePatchAppearance
        }
        return actor
    }

    fun addPose(eventName: String, pose: Pose) {
        getOrCreateEvent(eventName).poses.add(pose)
    }

    fun addTextStyle(eventName: String, textStyle: TextStyle) {
        getOrCreateEvent(eventName).textStyles.add(textStyle)
    }

    fun getOrCreateEvent(eventName: String): CostumeEvent {
        var event = events[eventName]
        if (event == null) {
            event = CostumeEvent()
            events[eventName] = event
        }
        return event
    }

    fun roleClass(): Class<*>? {
        if (roleString.isBlank()) return null

        try {
            return ScriptManager.classForName(roleString)
        } catch (e: Exception) {
            System.err.println("Warning. Costume '${Resources.instance.costumes.findName(this)}' couldn't create role '$roleString'. $e")
            return null
        }
    }

    fun createRole(): Role? {
        roleClass()?.let {
            Role.create(roleString)?.let { role ->
                attributes.applyToObject(role)
                return role
            }
        }
        return null
    }

    fun createChild(eventName: String): Actor {

        val childActor: Actor
        val newCostume = chooseCostume(eventName)

        if (newCostume != null) {
            val role = newCostume.createRole()
            childActor = Actor(newCostume, role)
            childActor.zOrder = newCostume.zOrder

            // Set the appearance. Either a Pose or a TextStyle (Pose takes precedence if it has both)
            val pose = newCostume.choosePose(newCostume.initialEventName)
            if (pose == null) {
                val style = newCostume.chooseTextStyle(newCostume.initialEventName)
                if (style != null) {
                    val text = newCostume.chooseString(newCostume.initialEventName) ?: ""
                    childActor.changeAppearance(text, style)
                } else {
                    val ninePatch = newCostume.chooseNinePatch(newCostume.initialEventName)
                    if (ninePatch != null) {
                        childActor.changeAppearance(ninePatch)
                    }
                }
            } else {
                childActor.changeAppearance(pose)
            }

        } else {
            childActor = Actor(this)
            childActor.event(eventName)
        }
        return childActor
    }

    /**
     * This is the pose used to display this costume from within the SceneEditor and CostumePickerBox.
     * This makes is easy to create invisible objects in the game, but visible in the editor.
     */
    fun editorPose(): Pose? = choosePose("editor") ?: choosePose(initialEventName)
    ?: chooseNinePatch(initialEventName)?.pose

    fun pose(): Pose? = choosePose(initialEventName)

    fun choosePose(eventName: String): Pose? = events[eventName]?.choosePose()
            ?: inheritEventsFrom?.choosePose(eventName)

    fun chooseNinePatch(eventName: String): NinePatch? = events[eventName]?.chooseNinePatch()
            ?: inheritEventsFrom?.chooseNinePatch(eventName)

    fun chooseCostume(eventName: String): Costume? = events[eventName]?.chooseCostume()
            ?: inheritEventsFrom?.chooseCostume(eventName)

    fun chooseTextStyle(eventName: String): TextStyle? = events[eventName]?.chooseTextStyle()
            ?: inheritEventsFrom?.chooseTextStyle(eventName)

    fun chooseString(eventName: String): String? = events[eventName]?.chooseString()
            ?: inheritEventsFrom?.chooseString(eventName)

    fun chooseSound(eventName: String): Sound? = events[eventName]?.chooseSound()
            ?: inheritEventsFrom?.chooseSound(eventName)


    // Copyable
    override fun copy(): Costume {
        val copy = Costume()
        copy.roleString = roleString
        copy.canRotate = canRotate
        copy.zOrder = zOrder
        copy.costumeGroup = costumeGroup
        copy.initialEventName = initialEventName

        attributes.map().forEach { name, data ->
            data.value?.let { copy.attributes.setValue(name, it) }
        }

        events.forEach { eventName, event ->
            val copyEvent = event.copy()
            copy.events[eventName] = copyEvent
        }

        copy.bodyDef = bodyDef?.copy()

        return copy
    }


    // Dependable

    fun dependsOn(pose: Pose): Boolean {
        events.values.forEach { event ->
            if (event.poses.contains(pose)) {
                return true
            }
            if (event.ninePatches.map { it.pose }.contains(pose)) {
                return true
            }
        }
        return false
    }

    fun dependsOn(fontResource: FontResource): Boolean {
        events.values.forEach { event ->
            if (event.textStyles.firstOrNull { it.fontResource === fontResource } != null) {
                return true
            }
        }
        return false
    }

    fun dependsOn(sound: Sound): Boolean {
        events.values.forEach { event ->
            if (event.sounds.contains(sound)) {
                return true
            }
        }
        return false
    }

    fun dependsOn(costume: Costume): Boolean {
        if (costume == this) return false

        if (inheritEventsFrom == costume) return true
        for (event in events.values) {
            if (event.costumes.contains(costume)) {
                return true
            }
        }
        return false
    }

    override fun isBreakable(dependency: Deletable): Boolean {
        return dependency is Pose || dependency is Costume || dependency is Sound
    }

    override fun breakDependency(dependency: Deletable) {
        when (dependency) {
            is Pose -> {
                for (event in events.values) {
                    event.poses.remove(dependency)
                    event.ninePatches.removeIf { it.pose == dependency }
                }
            }

            is Costume -> {
                for (event in events.values) {
                    event.costumes.remove(dependency)
                }
                if (inheritEventsFrom == dependency) {
                    inheritEventsFrom = null
                }
            }

            is FontResource -> {
                for (event in events.values) {
                    event.textStyles.removeIf { it.fontResource == dependency }
                }
            }

            is Sound -> {
                for (event in events.values) {
                    event.sounds.remove(dependency)
                }
            }
        }
    }

    // Deletable

    override fun dependables(): List<Dependable> {
        val result = mutableListOf<Dependable>()
        result.addAll(Resources.instance.costumes.items().values.filter { it.dependsOn(this) })

        for (stub in SceneStub.allScenesStubs()) {
            if (stub.dependsOn(this)) {
                result.add(stub)
            }
        }
        return result
    }


    override fun delete() {
        Resources.instance.costumes.remove(this)
        costumeGroup?.remove(this)
    }

    // Renamable
    override fun rename(newName: String) {
        Resources.instance.costumes.rename(this, newName)
        costumeGroup?.rename(this, newName)
    }


    override fun toString() = "Costume role='$roleString'. events=${events.values.joinToString()}"
}

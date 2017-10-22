package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Copyable

class Costume : Copyable<Costume> {

    var roleString: String = ""
        set(v) {
            if (field != v) {
                field = v
                attributes.updateAttributesMetaData(v, false)
            }
        }

    var canRotate: Boolean = false

    var zOrder: Double = 0.0

    val attributes = Attributes()

    val events = mutableMapOf<String, CostumeEvent>()

    var costumeGroup: CostumeGroup? = null

    var initialEventName: String = "default"

    var showInSceneEditor: Boolean = true

    fun createActor(text: String = ""): Actor {
        val role = if (roleString.isBlank()) null else Role.create(roleString)
        role?.let { attributes.applyToObject(it) }

        val actor = Actor(this, role)
        actor.zOrder = zOrder

        val pose = events[initialEventName]?.choosePose()
        if (pose == null) {
            val textStyle = events[initialEventName]?.chooseTextStyle()
            if (textStyle != null) {
                actor.changeAppearance(text, textStyle)
            }
        } else {
            actor.changeAppearance(pose)
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
            return Class.forName(roleString)
        } catch (e: Exception) {
            System.err.println("Warning. Costume '${Resources.instance.costumes.findName(this)}' couldn't create role '$roleString'. $e")
            return null
        }
    }

    fun createRole(): Role? {
        roleClass()?.let {
            return Role.create(roleString)
        }
        return null
    }

    fun createChild(eventName: String): Actor {
        events[eventName]?.let { event ->

            val newCostume = event.chooseCostume()
            if (newCostume != null) {
                val role = newCostume.createRole()
                val actor = Actor(newCostume, role)
                val defaultEvent = newCostume.events[newCostume.initialEventName]

                // Set the appearance. Either a Pose or a TextStyle (Pose takes precedence if it has both)
                val pose = defaultEvent?.choosePose()
                if (pose == null) {
                    val style = defaultEvent?.chooseTextStyle()
                    if (style != null) {
                        val text = defaultEvent.chooseString() ?: ""
                        actor.changeAppearance(text, style)
                    }
                } else {
                    actor.changeAppearance(pose)
                }
                return actor
            }

            val actor = Actor(this)
            event.choosePose()?.let { actor.changeAppearance(it) }
            return actor
        }
        return Actor(this)
    }

    /**
     * This is the pose used to display this costume from within the SceneEditor and CostumePickerBox.
     * This makes is easy to create invisible objects in the game, but visible in the editor.
     */
    fun editorPose(): Pose? = events.get("editor")?.choosePose() ?: events.get(initialEventName)?.choosePose()

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
        val events = mutableMapOf<String, CostumeEvent>()

        return copy
    }

    override fun toString() = "Costume role='$roleString'. events=${events.values.joinToString()}"
}

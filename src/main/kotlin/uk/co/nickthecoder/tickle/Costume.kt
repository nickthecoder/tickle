package uk.co.nickthecoder.tickle

import java.util.*

class Costume() {

    var roleString: String = ""

    val attributes = Attributes()
    val events = mutableMapOf<String, CostumeEvent>()

    // TODO Will have relatedCostumes later. And we can then use that to create bullets, explosions, etc.
    // RelatedCostumes should also store info about position and direction relative to the parent actor.

    fun createActor(): Actor {
        val role = if (roleString.isBlank()) null else Role.create(roleString)
        role?.let { attributes.applyToObject(it) }

        val actor = Actor(role)
        events["default"]?.choosePose()?.let { pose ->
            actor.changePose(pose)
        }
        return actor
    }

    override fun toString() = "Costume role='$roleString'. events=${events.values.joinToString()}"
}

class CostumeEvent {

    var poses = mutableListOf<Pose>()

    fun choosePose(): Pose? = if (poses.isEmpty()) null else poses[Random().nextInt(poses.size)]

    override fun toString() = "poses=$poses"
}

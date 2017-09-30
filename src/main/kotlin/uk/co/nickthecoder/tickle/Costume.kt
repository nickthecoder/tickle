package uk.co.nickthecoder.tickle

class Costume() {

    var roleString: String = ""

    val attributes = mutableMapOf<String,String>()

    val events = mutableMapOf<String, CostumeEvent>()

    // TODO Will have relatedCostumes later. And we can then use that to create bullets, explosions, etc.
    // RelatedCostumes should also store info about position and direction relative to the parent actor.

    fun createActor(): Actor {
        val role = if (roleString.isBlank()) null else Role.create(roleString)
        val actor = Actor(role)
        events["default"]?.choosePose()?.let { pose ->
            actor.changePose(pose)
        }
        return actor
    }

    override fun toString() = "Costume role='$roleString'. events=${events.values.joinToString()}"
}

class CostumeEvent {

    // TODO Will become a set of Poses later
    var pose: Pose? = null

    fun choosePose(): Pose? = pose

    override fun toString() = "pose=$pose"
}

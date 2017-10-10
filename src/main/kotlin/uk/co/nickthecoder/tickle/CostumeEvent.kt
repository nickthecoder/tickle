package uk.co.nickthecoder.tickle

import java.util.*


class CostumeEvent {

    var poses = mutableListOf<Pose>()

    var text = mutableListOf<String>()

    fun choosePose(): Pose? = if (poses.isEmpty()) null else poses[Random().nextInt(poses.size)]

    fun chooseText(): String? = if (text.isEmpty()) null else text[Random().nextInt(text.size)]

    override fun toString() = "poses=$poses"
}

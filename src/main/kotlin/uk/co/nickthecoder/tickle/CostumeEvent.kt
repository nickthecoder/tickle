package uk.co.nickthecoder.tickle

import java.util.*


class CostumeEvent {

    var poses = mutableListOf<Pose>()

    var fonts = mutableListOf<FontResource>()

    var strings = mutableListOf<String>()

    fun choosePose(): Pose? = if (poses.isEmpty()) null else poses[Random().nextInt(poses.size)]

    fun chooseFontResource(): FontResource? = if (fonts.isEmpty()) null else fonts[Random().nextInt(fonts.size)]

    fun chooseString(): String? = if (strings.isEmpty()) null else strings[Random().nextInt(strings.size)]

    override fun toString() = "poses=$poses"
}

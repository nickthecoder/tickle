package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.TextStyle
import java.util.*


class CostumeEvent {

    var poses = mutableListOf<Pose>()

    var textStyles = mutableListOf<TextStyle>()

    var strings = mutableListOf<String>()

    fun choosePose(): Pose? = if (poses.isEmpty()) null else poses[Random().nextInt(poses.size)]

    fun chooseTextStyle(): TextStyle? = if (textStyles.isEmpty()) null else textStyles[Random().nextInt(textStyles.size)]

    fun chooseString(): String? = if (strings.isEmpty()) null else strings[Random().nextInt(strings.size)]

    override fun toString() = "poses=$poses"
}

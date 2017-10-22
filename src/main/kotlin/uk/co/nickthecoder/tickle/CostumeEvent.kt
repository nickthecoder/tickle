package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.util.Copyable
import java.util.*


class CostumeEvent : Copyable<CostumeEvent> {

    var poses = mutableListOf<Pose>()

    var textStyles = mutableListOf<TextStyle>()

    var costumes = mutableListOf<Costume>()

    var strings = mutableListOf<String>()

    fun choosePose(): Pose? = if (poses.isEmpty()) null else poses[Random().nextInt(poses.size)]

    fun chooseCostume(): Costume? = if (costumes.isEmpty()) null else costumes[Random().nextInt(costumes.size)]

    fun chooseTextStyle(): TextStyle? = if (textStyles.isEmpty()) null else textStyles[Random().nextInt(textStyles.size)]

    fun chooseString(): String? = if (strings.isEmpty()) null else strings[Random().nextInt(strings.size)]

    override fun copy(): CostumeEvent {
        val copy = CostumeEvent()
        copy.textStyles.addAll(textStyles)
        copy.poses.addAll(poses)
        copy.costumes.addAll(costumes)
        copy.strings.addAll(strings)
        return copy

    }

    override fun toString() = "poses=$poses"
}

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
import uk.co.nickthecoder.tickle.sound.Sound
import uk.co.nickthecoder.tickle.util.Copyable
import java.util.*


class CostumeEvent : Copyable<CostumeEvent> {

    var poses = mutableListOf<Pose>()

    var textStyles = mutableListOf<TextStyle>()

    var costumes = mutableListOf<Costume>()

    var strings = mutableListOf<String>()

    var sounds = mutableListOf<Sound>()

    var ninePatches = mutableListOf<NinePatch>()

    fun choosePose(): Pose? = if (poses.isEmpty()) null else poses[Random().nextInt(poses.size)]

    fun chooseNinePatch(): NinePatch? = if (ninePatches.isEmpty()) null else ninePatches[Random().nextInt(ninePatches.size)]

    fun chooseCostume(): Costume? = if (costumes.isEmpty()) null else costumes[Random().nextInt(costumes.size)]

    fun chooseTextStyle(): TextStyle? = if (textStyles.isEmpty()) null else textStyles[Random().nextInt(textStyles.size)]

    fun chooseString(): String? = if (strings.isEmpty()) null else strings[Random().nextInt(strings.size)]

    fun chooseSound(): Sound? = if (sounds.isEmpty()) null else sounds[Random().nextInt(sounds.size)]

    override fun copy(): CostumeEvent {
        val copy = CostumeEvent()
        copy.textStyles.addAll(textStyles)
        copy.poses.addAll(poses)
        copy.costumes.addAll(costumes)
        copy.strings.addAll(strings)
        copy.sounds.addAll(sounds)
        return copy

    }

    override fun toString() = "poses=$poses costumes=$costumes strings=$strings sounds=$sounds"

}

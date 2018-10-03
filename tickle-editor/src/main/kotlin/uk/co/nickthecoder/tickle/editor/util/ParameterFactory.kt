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
package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.GroupedChoiceParameter
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.sound.Sound


fun createTextureParameter(parameterName: String = "texture"): ChoiceParameter<Texture?> {

    val parameter = ChoiceParameter<Texture?>(name = parameterName, required = true, value = null)

    Resources.instance.textures.items().forEach { name, texture ->
        parameter.addChoice(name, texture, name)
    }
    return parameter
}


fun createPoseParameter(parameterName: String = "pose", label: String = "Pose", required: Boolean = true): GroupedChoiceParameter<Pose?> {

    val choiceParameter = GroupedChoiceParameter<Pose?>(parameterName, label = label, required = required, value = null)

    if (!required) {
        choiceParameter.addChoice("", null, "None")
    }
    Resources.instance.textures.items().forEach { textureName, texture ->
        val group = choiceParameter.group(textureName)
        Resources.instance.poses.items().filter { it.value.texture === texture }.forEach { poseName, pose ->
            group.choice(poseName, pose, poseName)
        }

    }
    return choiceParameter
}


fun createFontParameter(parameterName: String = "font", label: String = "Font"): ChoiceParameter<FontResource?> {

    val choice = ChoiceParameter<FontResource?>(parameterName, label = label, required = true, value = null)

    Resources.instance.fontResources.items().forEach { name, fontResource ->
        choice.addChoice(name, fontResource, name)
    }
    return choice
}


fun createCostumeParameter(parameterName: String = "costume", required: Boolean = true, value: Costume? = null): GroupedChoiceParameter<Costume?> {

    val choiceParameter = GroupedChoiceParameter<Costume?>(parameterName, required = required, value = value)

    if (!required) {
        choiceParameter.addChoice("", null, "None")
    }

    val defaultGroup = choiceParameter.group("")
    Resources.instance.costumes.items().filter { it.value.costumeGroup == null }.forEach { costumeName, costume ->
        defaultGroup.choice(costumeName, costume, costumeName)
    }
    Resources.instance.costumeGroups.items().forEach { groupName, costumeGroup ->
        val group = choiceParameter.group(groupName)
        costumeGroup.items().forEach { costumeName, costume ->
            group.choice(costumeName, costume, costumeName)
        }
    }
    return choiceParameter
}


fun createSoundParameter(parameterName: String = "sound"): ChoiceParameter<Sound?> {

    val choice = ChoiceParameter<Sound?>(parameterName, required = true, value = null)

    Resources.instance.sounds.items().forEach { name, sound ->
        choice.addChoice(name, sound, name)
    }
    return choice
}

fun createNinePatchParameter(parameterName: String = "ninePatch", label: String = "NinePatch") = NinePatchParameter(parameterName, label)

package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
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


fun createPoseParameter(parameterName: String = "pose", label: String = "Pose", required: Boolean = true): ChoiceParameter<Pose?> {

    val choice = ChoiceParameter<Pose?>(parameterName, label = label, required = required, value = null)

    if (!required) {
        choice.addChoice("", null, "None")
    }
    Resources.instance.poses.items().forEach { poseName, pose ->
        choice.addChoice(poseName, pose, poseName)
    }
    return choice
}


fun createFontParameter(parameterName: String = "font", label: String = "Font"): ChoiceParameter<FontResource?> {

    val choice = ChoiceParameter<FontResource?>(parameterName, label = label, required = true, value = null)

    Resources.instance.fontResources.items().forEach { name, fontResource ->
        choice.addChoice(name, fontResource, name)
    }
    return choice
}


fun createCostumeParameter(parameterName: String = "costume", required: Boolean = true, value: Costume? = null): ChoiceParameter<Costume?> {

    val choice = ChoiceParameter<Costume?>(parameterName, required = required, value = value)

    if (!required) {
        choice.addChoice("", null, "None")
    }
    Resources.instance.costumes.items().forEach { costumeName, costume ->
        choice.addChoice(costumeName, costume, costumeName)
    }
    return choice
}


fun createSoundParameter(parameterName: String = "sound"): ChoiceParameter<Sound?> {

    val choice = ChoiceParameter<Sound?>(parameterName, required = true, value = null)

    Resources.instance.sounds.items().forEach { name, sound ->
        choice.addChoice(name, sound, name)
    }
    return choice
}

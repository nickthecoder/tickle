package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Resources


fun createTextureParameter(parameterName: String = "texture"): ChoiceParameter<Texture?> {

    val parameter = ChoiceParameter<Texture?>(name = parameterName, required = true, value = null)

    Resources.instance.textures.items().forEach { name, texture ->
        parameter.addChoice(name, texture, name)
    }
    return parameter
}


fun createPoseParameter(parameterName: String = "pose"): ChoiceParameter<Pose?> {

    val choice = ChoiceParameter<Pose?>(parameterName, required = true, value = null)

    Resources.instance.poses.items().forEach { poseName, pose ->
        choice.addChoice(poseName, pose, poseName)
    }
    return choice
}


fun createFontParameter(parameterName: String = "font"): ChoiceParameter<FontResource?> {

    val choice = ChoiceParameter<FontResource?>(parameterName, required = true, value = null)

    Resources.instance.fontResources.items().forEach { name, fontResource ->
        choice.addChoice(name, fontResource, name)
    }
    return choice
}


fun createCostumeParameter(parameterName: String = "costume"): ChoiceParameter<Costume?> {

    val choice = ChoiceParameter<Costume?>(parameterName, required = true, value = null)

    Resources.instance.costumes.items().forEach { costumeName, costume ->
        choice.addChoice(costumeName, costume, costumeName)
    }
    return choice
}

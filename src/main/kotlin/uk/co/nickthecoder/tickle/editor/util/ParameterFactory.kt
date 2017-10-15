package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.graphics.Texture


fun createTextureParameter(parameterName: String = "texture"): ChoiceParameter<Texture?> {

    val parameter = ChoiceParameter<Texture?>(name = parameterName, required = true, value = null)

    Resources.instance.textures().forEach { name, texture ->
        parameter.addChoice(name, texture, name)
    }
    return parameter
}


fun createPoseParameter(parameterName: String = "pose"): ChoiceParameter<Pose?> {

    val choice = ChoiceParameter<Pose?>(parameterName, required = true, value = null)

    Resources.instance.poses().forEach { poseName, pose ->
        choice.addChoice(poseName, pose, poseName)
    }
    return choice
}


fun createFontParameter(parameterName: String = "font"): ChoiceParameter<FontResource?> {

    val choice = ChoiceParameter<FontResource?>(parameterName, required = true, value = null)

    Resources.instance.fontResources().forEach { name, fontResource ->
        choice.addChoice(name, fontResource, name)
    }
    return choice
}

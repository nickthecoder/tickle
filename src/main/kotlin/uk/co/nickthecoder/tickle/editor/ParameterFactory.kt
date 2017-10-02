package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.graphics.Texture


fun createTextureParameter(parameterName: String = "texture"): ChoiceParameter<Texture?> {

    val parameter = ChoiceParameter<Texture?>(name = parameterName, required = true, value = null)

    Resources.instance.textures().forEach { name, textureResource ->
        parameter.addChoice(name, textureResource.texture, name)
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

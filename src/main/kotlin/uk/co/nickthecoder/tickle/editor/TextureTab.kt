package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.TextureResource

class TextureTab(val resources: Resources, name: String, texture: TextureResource)

    : TaskTab(TextureTask(name, texture), "Texture $name", texture) {

}

class TextureTask(val name: String, val textureResource: TextureResource) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    override val taskD = TaskDescription("editPose")
            .addParameters(nameP)

    init {
    }

    override fun run() {
        if (nameP.value != name) {
            // TODO Rename!
        }
    }
}

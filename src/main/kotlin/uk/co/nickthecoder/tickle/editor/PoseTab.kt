package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources

class PoseTab(val resources: Resources, name: String, pose: Pose)

    : TaskTab(PoseTask(name, pose), "Pose $name", pose) {

}

class PoseTask(val name: String, val pose: Pose) : AbstractTask() {

    val nameP = StringParameter("name", value = name)
    val positionP = RectParameter("position", bottomUp = false)
    val offsetP = XYParameter("offset")

    override val taskD = TaskDescription("editPose")
            .addParameters(nameP, positionP, offsetP)

    init {
        offsetP.x = pose.offsetX.toDouble()
        offsetP.y = pose.offsetY.toDouble()

        positionP.left = pose.rect.left
        positionP.bottom = pose.rect.bottom
        positionP.right = pose.rect.right
        positionP.top = pose.rect.top
    }

    override fun run() {
        if (nameP.value != name) {
            // TODO Rename!
        }
        pose.rect.left = positionP.left!!
        pose.rect.bottom = positionP.bottom!!
        pose.rect.right = positionP.right!!
        pose.rect.top = positionP.top!!

        pose.offsetX = offsetP.x!!.toFloat()
        pose.offsetY = offsetP.y!!.toFloat()
    }
}

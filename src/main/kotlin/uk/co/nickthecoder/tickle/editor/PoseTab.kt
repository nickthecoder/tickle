package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources

class PoseTab(name: String, pose: Pose)

    : TaskTab(PoseTask(name, pose), "Pose $name", pose) {

}

class PoseTask(val name: String, val pose: Pose) : AbstractTask() {

    val nameP = StringParameter("name", value = name)
    val positionP = RectiParameter("position", bottomUp = false)
    val offsetP = XYParameter("offset")

    override val taskD = TaskDescription("editPose")
            .addParameters(nameP, positionP, offsetP)

    init {
        offsetP.x = pose.offsetX.toDouble()
        offsetP.y = pose.offsetY.toDouble()

        positionP.left = pose.rect.left
        positionP.right = pose.rect.right
        positionP.top = pose.rect.top
        positionP.bottom = pose.rect.bottom

        // println("Init LTRB : ${positionP.left},${positionP.top}, ${positionP.right}, ${positionP.bottom}  size : ${positionP.width}, ${positionP.height}")

    }

    override fun customCheck() {
        val p = Resources.instance.optionalPose(nameP.value)
        if (p != null && p != pose) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        //println("Run LTRB : ${positionP.left},${positionP.top}, ${positionP.right}, ${positionP.bottom}  size : ${positionP.width}, ${positionP.height}")
        if (nameP.value != name) {
            Resources.instance.renamePose(name, nameP.value)
        }
        pose.rect.left = positionP.left!!
        pose.rect.bottom = positionP.bottom!!
        pose.rect.right = positionP.right!!
        pose.rect.top = positionP.top!!

        pose.offsetX = offsetP.x!!.toFloat()
        pose.offsetY = offsetP.y!!.toFloat()
    }
}

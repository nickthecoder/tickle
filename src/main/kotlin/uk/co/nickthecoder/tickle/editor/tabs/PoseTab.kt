package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ButtonParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.ImageCache
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.util.ImageParameter
import uk.co.nickthecoder.tickle.editor.util.ImageParameterField
import uk.co.nickthecoder.tickle.editor.util.RectiParameter
import uk.co.nickthecoder.tickle.editor.util.XYParameter

class PoseTab(name: String, pose: Pose)
    : EditTaskTab(PoseTask(name, pose), "Pose", name, pose, graphicName = "pose.png") {

    init {
        addDeleteButton { Resources.instance.deletePose(name) }
        val createCostumeButton = Button("Create Costume")
        createCostumeButton.setOnAction { (task as PoseTask).createCostume() }
        leftButtons.children.add(createCostumeButton)
    }


}

class PoseTask(val name: String, val pose: Pose) : AbstractTask() {

    val nameP = StringParameter("name", value = name)
    val textureNameP = ButtonParameter("texture", buttonText = Resources.instance.findTextureName(pose.texture) ?: "<none>") {
        editTexture()
    }

    val positionP = RectiParameter("position", bottomUp = false)

    val offsetInfoP = InformationParameter("offsetInfo", information = "Offsets are measured from the BOTTOM left.")

    val offsetP = XYParameter("offset")

    val directionP = DoubleParameter("direction", description = "The direction of the pose in degrees. 0 is to the right, and +ve numbers are anti-clockwise.")

    val infoP = InformationParameter("info", information = "Click the image to set the offsets. Right click to change the background colour.")

    val imageP = ImageParameter("image", image = ImageCache.image(pose.texture.file!!)) { PoseImageField(it) }

    override val taskD = TaskDescription("editPose")
            .addParameters(nameP, textureNameP, positionP, offsetInfoP, offsetP, directionP, imageP, infoP)

    init {
        offsetP.x = pose.offsetX.toDouble()
        offsetP.y = pose.offsetY.toDouble()

        positionP.left = pose.rect.left
        positionP.right = pose.rect.right
        positionP.top = pose.rect.top
        positionP.bottom = pose.rect.bottom

        directionP.value = pose.directionDegrees

        updateViewport()

        positionP.listen {
            updateViewport()
        }
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

        pose.directionDegrees = directionP.value!!
    }

    fun editTexture() {
        val trName = Resources.instance.findTextureName(pose.texture)
        if (trName != null) {
            val tab = TextureTab(trName, pose.texture)
            MainWindow.instance?.tabPane?.add(tab)
            tab.isSelected = true
        }
    }

    fun createCostume() {
        val poseName = Resources.instance.findPoseName(pose)
        if (poseName == null) {
            return
        }

        if (Resources.instance.optionalCostume(poseName) != null) {
            Alert(Alert.AlertType.INFORMATION, "A Costume called ${name} already exists.").showAndWait()
            return
        }

        val costume = Costume()
        costume.addPose("default", pose)
        Resources.instance.addCostume(poseName, costume)
        MainWindow.instance?.openTab(poseName, costume)
    }


    fun updateViewport() {
        val left = positionP.left
        val top = positionP.top
        val width = positionP.width
        val height = positionP.height

        if (left == null || top == null || width == null || height == null) {
            imageP.viewPort = Rectangle2D.EMPTY
        } else {
            imageP.viewPort = Rectangle2D(left.toDouble(), top.toDouble(), width.toDouble(), height.toDouble())
        }

    }

    inner class PoseImageField(imageParameter: ImageParameter) : ImageParameterField(imageParameter) {

        override fun createControl(): Node {
            val iv = super.createControl()
            val stack = StackPane()

            val colors = listOf(Color.LIGHTGRAY, Color.DARKGRAY, Color.BLACK, Color.WHITE)
            var colorIndex = 0

            var isLight = true

            stack.addEventHandler(MouseEvent.MOUSE_PRESSED) { event ->
                if (event.button == MouseButton.SECONDARY) {
                    colorIndex++
                    if (colorIndex >= colors.size) colorIndex = 0

                    stack.background = Background(BackgroundFill(colors[colorIndex], CornerRadii(0.0), Insets(0.0)))
                } else {
                    positionP.height?.let {
                        offsetP.x = event.x
                        offsetP.y = it - event.y
                    }
                }
            }

            stack.background = Background(BackgroundFill(colors[colorIndex], CornerRadii(0.0), Insets(0.0)))
            stack.style = "-fx-cursor: crosshair;"
            stack.children.add(iv)

            return stack
        }
    }
}

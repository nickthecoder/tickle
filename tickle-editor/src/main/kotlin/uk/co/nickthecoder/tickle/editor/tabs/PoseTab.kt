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
package uk.co.nickthecoder.tickle.editor.tabs

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
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
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.editor.util.*
import uk.co.nickthecoder.tickle.resources.Resources

class PoseTab(name: String, val pose: Pose)
    : EditTaskTab(PoseTask(name, pose), name, pose, graphicName = "pose.png") {

    val costumesButton = MenuButton("Costumes")

    init {

        addCopyButton(pose, ResourceType.COSTUME) { newName, newPose ->
            Resources.instance.poses.add(newName, newPose)
        }

        val createCostumeButton = Button("Create Costume")
        createCostumeButton.setOnAction { (task as PoseTask).createCostume() }
        leftButtons.children.add(createCostumeButton)

        buildCostumesButton()
    }

    private fun buildCostumesButton() {
        val costumes = mutableMapOf<String, Costume>()
        Resources.instance.costumes.items().forEach { name, costume ->
            if (costume.dependsOn(pose)) {
                costumes[name] = costume
            }
        }

        if (costumes.isEmpty()) {
            leftButtons.children.remove(costumesButton)
        } else {
            costumesButton.items.clear()
            costumes.forEach { costumeName, costume ->
                val menuItem = MenuItem(costumeName)
                costumesButton.items.add(menuItem)
                menuItem.onAction = EventHandler { MainWindow.instance.openTab(costumeName, costume) }
            }
            if (!leftButtons.children.contains(costumesButton)) {
                leftButtons.children.add(costumesButton)
            }
        }
    }

}

class PoseTask(val name: String, val pose: Pose) : AbstractTask() {

    val nameP = StringParameter("name", value = name)
    val textureNameP = ButtonParameter("texture", buttonText = Resources.instance.textures.findName(pose.texture) ?: "<none>") {
        editTexture()
    }

    val positionP = RectiParameter("position", bottomUp = false)

    val offsetInfoP = InformationParameter("offsetInfo", information = "Offsets are measured from the BOTTOM left.")

    val offsetP = XYParameter("offset")

    val directionP = DoubleParameter("direction", description = "The direction of the pose in degrees. 0 is to the right, and +ve numbers are anti-clockwise.")

    val tiledP = BooleanParameter("tiled")

    val imageP = ImageParameter("image", image = ImageCache.image(pose.texture.file!!)) { PoseImageField(it) }

    val infoP = InformationParameter("info", information = "Click the image to set the offsets. Right click to change the background colour.")

    val snapPointsP = MultipleParameter("snapPoints", isBoxed = true) {
        Vector2dParameter("snapPoint").asHorizontal()
    }

    override val taskD = TaskDescription("editPose")
            .addParameters(nameP, textureNameP, positionP, offsetInfoP, offsetP, directionP, tiledP, imageP, infoP, snapPointsP)

    init {
        offsetP.x = pose.offsetX.toDouble()
        offsetP.y = pose.offsetY.toDouble()

        positionP.left = pose.rect.left
        positionP.right = pose.rect.right
        positionP.top = pose.rect.top
        positionP.bottom = pose.rect.bottom

        directionP.value = pose.direction.degrees
        tiledP.value = pose.tiled

        snapPointsP.clear()
        pose.snapPoints.forEach { snapPointsP.addValue(it) }

        updateViewport()

        positionP.listen {
            updateViewport()
        }

        // println("Init LTRB : ${positionP.left},${positionP.top}, ${positionP.right}, ${positionP.bottom}  size : ${positionP.width}, ${positionP.height}")
    }

    override fun customCheck() {
        val p = Resources.instance.poses.find(nameP.value)
        if (p != null && p != pose) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        //println("Run LTRB : ${positionP.left},${positionP.top}, ${positionP.right}, ${positionP.bottom}  size : ${positionP.width}, ${positionP.height}")
        if (nameP.value != name) {
            Resources.instance.poses.rename(name, nameP.value)
        }
        pose.rect.left = positionP.left!!
        pose.rect.bottom = positionP.bottom!!
        pose.rect.right = positionP.right!!
        pose.rect.top = positionP.top!!

        pose.offsetX = offsetP.x!!
        pose.offsetY = offsetP.y!!

        pose.direction.degrees = directionP.value!!
        pose.tiled = tiledP.value == true

        pose.snapPoints.clear()
        pose.snapPoints.addAll(snapPointsP.value)
    }

    fun editTexture() {
        val trName = Resources.instance.textures.findName(pose.texture)
        if (trName != null) {
            MainWindow.instance.openTab(trName, pose.texture)
        }
    }

    fun createCostume() {
        val task = NewResourceTask(pose, nameP.value)
        task.prompt()
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

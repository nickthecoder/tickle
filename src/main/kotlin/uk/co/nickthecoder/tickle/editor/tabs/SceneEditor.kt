package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.editor.ImageCache


class SceneEditor(val sceneResource: SceneResource) {

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    val gameWidth = Resources.instance.gameInfo.width.toDouble()
    val gameHeight = Resources.instance.gameInfo.height.toDouble()

    val canvas = Canvas(gameWidth, gameHeight)

    var gc = canvas.graphicsContext2D

    fun build(): Node {

        with(scrollPane) {
            content = canvas
        }

        with(borderPane) {
            center = scrollPane
        }

        gc.transform(1.0, 0.0, 0.0, -1.0, 0.0, canvas.height)
        draw()
        return borderPane
    }

    fun draw() {

        drawBorder()

        gc.fill = Color.BLUE
        gc.fillRect(75.0, 75.0, 100.0, 10.00)

        sceneResource.sceneStages.forEach { name, sceneStage ->
            sceneStage.sceneActors.forEach { sceneActor ->
                pose(sceneActor)?.let { pose ->
                    gc.save()
                    gc.translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                    drawPose(pose)
                    gc.restore()
                }
            }
        }

    }

    fun drawBorder() {
        gc.stroke = Color.LIGHTCORAL
        gc.lineWidth = 1.0
        gc.setLineDashes(10.0, 3.0)
        gc.moveTo(0.0, 0.0)
        gc.lineTo(canvas.width, 0.0)
        gc.lineTo(canvas.width, canvas.height)
        gc.lineTo(0.0, canvas.height)
        gc.lineTo(0.0, 0.0)
        gc.stroke()
    }

    fun drawPose(pose: Pose) {
        val image = image(pose)
        gc.drawImage(
                image,
                pose.rect.left.toDouble(), pose.rect.top.toDouble(), pose.rect.width.toDouble(), pose.rect.height.toDouble(),
                -pose.offsetX.toDouble(), -pose.offsetY.toDouble(), pose.rect.width.toDouble(), pose.rect.height.toDouble())
    }

    fun image(pose: Pose): Image? {
        pose.texture.file?.let {
            return ImageCache.image(it)
        }
        return null
    }

    fun pose(sceneActor: SceneActor): Pose? {
        return Resources.instance.optionalCostume(sceneActor.costumeName)?.events?.get("default")?.choosePose()
    }
}


import javafx.scene.canvas.Canvas
import javafx.scene.effect.BoxBlur
import javafx.scene.effect.Effect
import javafx.scene.paint.Color
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.util.RandomFactory

class LineStyle(val width: Double, val effect: Effect?, val color: Color)

/**
 * Creates a plasma effect. I run this interactively using [uk.co.nickthecoder.tickle.editor.FXCoder].
 * Make sure to uncomment the final line!
 */
class PlasmaScript {

    val rand = RandomFactory(11L)

    /**
     * Size of a single image.
     */
    val w = 300.0
    val h = 120.0

    /**
     * The number of images generated (will be tiled vertically).
     */
    val frames = 4

    /**
     * Maximum random value added to the y values
     */
    var deltaY = 30.0

    /**
     * An exponential value determining the number of line segments. Adding one will double the number of line segments.
     */
    val iterations = 4

    /**
     * Each line is drawn multiple times, each with different line widths, blurs, and colors.
     * This is the styles of the front-most plasma, and is the thickest and most prominent
     */
    val majorLineStyles = listOf<LineStyle>(
            LineStyle(32.0, BoxBlur(16.0, 16.0, 4), Color(0.1, 0.1, 1.0, 0.5)),
            LineStyle(16.0, BoxBlur(16.0, 16.0, 2), Color(0.1, 0.1, 1.0, 0.8)),
            LineStyle(10.0, BoxBlur(8.0, 8.0, 2), Color(0.3, 0.3, 1.0, 1.0)),
            LineStyle(3.0, null, Color.WHITE)
    )
    /**
     * A thinner and less prominent style than the mojorLineStyles.
     */
    val minorLineStyles1 = listOf<LineStyle>(
            LineStyle(5.0, BoxBlur(3.0, 3.0, 2), Color(0.3, 0.3, 1.0, 0.5)),
            LineStyle(1.0, null, Color(0.5, 0.5, 1.0, 1.0))
    )
    /**
     * The darkest and thin style
     */
    val minorLineStyles2 = listOf<LineStyle>(
            LineStyle(5.0, BoxBlur(3.0, 3.0, 2), Color(0.0, 0.0, 0.4, 0.5)),
            LineStyle(1.0, null, Color(0.2, 0.2, 0.6, 1.0))
    )

    val points = mutableListOf<Vector2d>()
    val canvas = Canvas(w, h * frames)

    /**
     * Binary chops from the two ends, adding a random factor to the x and y values.
     */
    fun createPoints() {
        points.clear()
        val start = Vector2d(0.0, h / 2)
        val end = Vector2d(w, h / 2)
        points.add(start)
        points.add(end)
        addPoints(start, end, deltaY, iterations)
        points.sortBy { it.x }
    }

    fun addPoints(start: Vector2d, end: Vector2d, dy: Double, i: Int) {
        val newPoint = Vector2d()
        start.add(end, newPoint).mul(0.5).add(0.0, rand.plusMinus(dy))
        points.add(newPoint)
        if (i > 0) {
            addPoints(start, newPoint, dy / 2, i - 1)
            addPoints(newPoint, end, dy / 2, i - 1)
        }
    }

    fun blackBackground() {
        with(canvas.graphicsContext2D) {
            fill = Color.BLACK
            fillRect(0.0, 0.0, w, h * frames)
        }
    }

    fun draw(): Canvas {

        //blackBackground()

        for (frame in 0..frames) {
            drawArc(minorLineStyles2)
            drawArc(minorLineStyles1)
            drawArc(majorLineStyles)
            // Move down, so that the next frame does not overlap this one.
            canvas.graphicsContext2D.translate(0.0, h)
        }

        return canvas
    }

    fun drawArc(lineStyles: List<LineStyle>) {

        createPoints()

        with(canvas.graphicsContext2D) {

            lineStyles.forEach { lineStyle ->
                save()
                beginPath()
                moveTo(points[0].x, points[0].y)
                lineWidth = lineStyle.width
                setEffect(lineStyle.effect)
                stroke = lineStyle.color

                points.forEach { point ->
                    lineTo(point.x, point.y)
                }
                stroke()
                restore()
            }
        }

    }
}

// PlasmaScript().draw()

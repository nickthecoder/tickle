package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Vec2
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.util.Angle


class BoxDef(
        var width: Double,
        var height: Double,
        var center: Vector2d,
        var angle: Angle,
        var cornerRadius: Double,
        var roundedEnds: Boolean = false)

    : ShapeDef {

    override fun createShapes(world: TickleWorld): List<Shape> {

        if (roundedEnds) {
            return createRoundedEnds(world)
        } else {
            if (cornerRadius == 0.0) {
                return createBox(world)
            } else {
                return createRoundedBox(world)
            }
        }
    }

    private fun createRoundedEnds(world: TickleWorld): List<Shape> {

        val box = PolygonShape()

        val circle1 = CircleShape()
        val circle2 = CircleShape()

        val sin = Math.sin(angle.radians)
        val cos = Math.cos(angle.radians)

        if (width > height) {
            val radius = height / 2
            val rectWidth = width - radius * 2
            box.setAsBox(
                    world.pixelsToWorld(rectWidth / 2),
                    world.pixelsToWorld(height / 2),
                    world.pixelsToWorld(center),
                    angle.radians.toFloat())

            val circleX = rectWidth / 2
            val rotatedX = circleX * cos
            val rotatedY = circleX * sin
            circle1.m_p.x = world.pixelsToWorld(center.x + rotatedX)
            circle1.m_p.x = world.pixelsToWorld(center.y + rotatedY)
            circle1.m_radius = world.pixelsToWorld(radius)

            circle2.m_p.x = world.pixelsToWorld(center.x - rotatedY)
            circle2.m_p.y = world.pixelsToWorld(center.y - rotatedY)
            circle2.m_radius = world.pixelsToWorld(radius)

        } else {
            val radius = width / 2
            val rectHeight = height - radius * 2

            box.setAsBox(
                    world.pixelsToWorld(width / 2),
                    world.pixelsToWorld(rectHeight / 2),
                    world.pixelsToWorld(center),
                    angle.radians.toFloat())

            val circleY = rectHeight / 2
            val rotatedY = circleY * cos
            val rotatedX = circleY * sin
            circle1.m_p.x = world.pixelsToWorld(center.x + rotatedX)
            circle1.m_p.y = world.pixelsToWorld(center.y + rotatedY)
            circle1.m_radius = world.pixelsToWorld(radius)

            circle2.m_p.x = world.pixelsToWorld(center.x - rotatedX)
            circle2.m_p.y = world.pixelsToWorld(center.y - rotatedY)
            circle2.m_radius = world.pixelsToWorld(radius)
        }

        return listOf(box, circle1, circle2)
    }

    private fun createBox(world: TickleWorld): List<Shape> {
        val box = PolygonShape()

        box.setAsBox(
                world.pixelsToWorld(width / 2),
                world.pixelsToWorld(height / 2),
                world.pixelsToWorld(center),
                angle.radians.toFloat())

        return listOf(box)
    }

    private fun createRoundedBox(world: TickleWorld): List<Shape> {

        val middleHalfWidth = world.pixelsToWorld(width / 2 - cornerRadius)
        val middleHalfHeight = world.pixelsToWorld(height / 2 - cornerRadius)

        val worldRadius = world.pixelsToWorld(cornerRadius)
        val worldAngle = angle.radians.toFloat()
        val worldCenter = world.pixelsToWorld(center)

        val cos = Math.cos(angle.radians).toFloat()
        val sin = Math.sin(angle.radians).toFloat()

        // For the centers of the corner circles
        val cornerDx = Vec2(middleHalfWidth * cos, middleHalfWidth * sin)
        val cornerDy = Vec2(middleHalfHeight * sin, middleHalfHeight * cos)

        // For the centers of the edges
        val edgeDx = Vec2((middleHalfWidth + worldRadius / 2) * cos, (middleHalfWidth + worldRadius / 2) * sin)
        val edgeDy = Vec2((middleHalfHeight + worldRadius / 2) * sin, (middleHalfHeight + worldRadius / 2) * cos)

        val middle = PolygonShape()
        middle.setAsBox(
                middleHalfWidth,
                middleHalfHeight,
                worldCenter,
                worldAngle)

        val left = PolygonShape()
        left.setAsBox(
                worldRadius / 2,
                middleHalfHeight,
                worldCenter.sub(edgeDx),
                worldAngle)

        val right = PolygonShape()
        right.setAsBox(
                worldRadius / 2,
                middleHalfHeight,
                worldCenter.add(edgeDx),
                worldAngle)

        val top = PolygonShape()
        top.setAsBox(
                middleHalfWidth,
                worldRadius / 2,
                worldCenter.add(edgeDy),
                worldAngle)

        val bottom = PolygonShape()
        bottom.setAsBox(
                middleHalfWidth,
                worldRadius / 2,
                worldCenter.sub(edgeDy),
                worldAngle)

        val corner1 = CircleShape()
        val pos1 = worldCenter.sub(cornerDx).sub(cornerDy)
        corner1.m_p.x = pos1.x
        corner1.m_p.y = pos1.y
        corner1.m_radius = worldRadius

        val corner2 = CircleShape()
        val pos2 = worldCenter.sub(cornerDx).add(cornerDy)
        corner2.m_p.x = pos2.x
        corner2.m_p.y = pos2.y
        corner2.m_radius = worldRadius

        val corner3 = CircleShape()
        val pos3 = worldCenter.add(cornerDx).sub(cornerDy)
        corner3.m_p.x = pos3.x
        corner3.m_p.y = pos3.y
        corner3.m_radius = worldRadius

        val corner4 = CircleShape()
        val pos4 = worldCenter.add(cornerDx).add(cornerDy)
        corner4.m_p.x = pos4.x
        corner4.m_p.y = pos4.y
        corner4.m_radius = worldRadius

        return listOf(middle, top, bottom, left, right, corner1, corner2, corner3, corner4)
    }

}

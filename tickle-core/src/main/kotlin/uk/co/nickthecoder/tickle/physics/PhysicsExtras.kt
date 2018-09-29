package uk.co.nickthecoder.tickle.physics

import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.FixtureDef


fun BodyType.hasFixtures() = this == BodyType.DYNAMIC || this == BodyType.STATIC

fun Fixture.offset(body: Body, dx: Float, dy: Float) {
    val shape = shape

    var newShape: Shape? = null

    when (shape) {
        is CircleShape -> {
            shape.m_p.x += dx
            shape.m_p.y += dy
        }
        is PolygonShape -> {
            val points = Array<Vec2>(shape.vertexCount) { Vec2(shape.vertices[it].x + dx, shape.vertices[it].y + dy) }

            newShape = PolygonShape()
            newShape.set(points, shape.vertexCount)
        }
    }

    if (newShape != null) {
        val fixtureDef = FixtureDef()
        fixtureDef.filter = m_filter
        fixtureDef.friction = friction
        fixtureDef.density = density
        fixtureDef.restitution = restitution
        fixtureDef.isSensor = isSensor
        fixtureDef.userData = userData
        fixtureDef.shape = newShape
        body.destroyFixture(this)
        body.createFixture(fixtureDef)
    }
}

fun Body.scale(scaleX: Float, scaleY: Float) {

    // We cannot just iterate through the fixtures, because to scale the fixtures, we need to delete the old
    // ones and create new ones. So get all the fixtures first...
    val fixtures = mutableListOf<Fixture>()
    var fixture = fixtureList
    while (fixture != null) {
        fixtures.add(fixture)
        fixture = fixture.next
    }
    fixtures.forEach { it.scale(this, scaleX, scaleY) }
    resetMassData()
    isAwake = true
}

fun Body.offset(dx: Float, dy: Float) {

    // We cannot just iterate through the fixtures, because we need to delete the old
    // ones and create new ones. So get all the fixtures first...
    val fixtures = mutableListOf<Fixture>()
    var fixture = fixtureList
    while (fixture != null) {
        fixtures.add(fixture)
        fixture = fixture.next
    }
    fixtures.forEach { it.offset(this, dx, dy) }
    resetMassData()
    isAwake = true
}

/**
 * Note. If scaleX != scaleY, then any CircleShapes will be converted to polygons
 * (because ellipses aren't supported by JBox2d).
 */
fun Fixture.scale(body: Body, scaleX: Float, scaleY: Float) {
    val shape = shape

    var newShape: Shape? = null

    when (shape) {
        is CircleShape -> {
            if (scaleX == scaleY) {
                shape.m_radius *= scaleX
                shape.m_p.x *= scaleX
                shape.m_p.y *= scaleY
            } else {
                // Ellipses aren't supported, so lets convert the circle to a polygon
                val count = 8
                val points = Array<Vec2>(count) {
                    val angle = it * Math.PI * 2 / count
                    Vec2((Math.cos(angle) * shape.m_radius * scaleX).toFloat(), (Math.sin(angle) * shape.m_radius * scaleY).toFloat())
                }
                newShape = PolygonShape()
                newShape.set(points, count)
            }
        }
        is PolygonShape -> {
            val points = Array<Vec2>(shape.vertexCount) { Vec2(shape.vertices[it].x * scaleX, shape.vertices[it].y * scaleY) }
            // If one scale is negative, then we need to reverse the points in order to preserve the anti-clockwise order of the points.
            if ((scaleX < 0).xor(scaleY < 0)) {
                points.reverse()
            }
            newShape = PolygonShape()
            newShape.set(points, shape.vertexCount)
        }
    }

    if (newShape != null) {
        val fixtureDef = FixtureDef()
        fixtureDef.filter = m_filter
        fixtureDef.friction = friction
        fixtureDef.density = density
        fixtureDef.restitution = restitution
        fixtureDef.isSensor = isSensor
        fixtureDef.userData = userData
        fixtureDef.shape = newShape
        body.destroyFixture(this)
        body.createFixture(fixtureDef)
    }
}


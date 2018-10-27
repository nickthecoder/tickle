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
package uk.co.nickthecoder.tickle.resources

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.util.Angle

enum class ActorXAlignment { LEFT, CENTER, RIGHT, RATIO }
enum class ActorYAlignment { BOTTOM, CENTER, TOP, RATIO }

/**
 * Details of an Actor's initial state.
 * Used when loading and editing a Scene. Not used during actual game play.
 */

open class ActorResource(val isDesigning: Boolean = false)

    : ActorDetails {

    var costumeName: String = ""
        set(v) {
            field = v
            updateAttributesMetaData()
            Resources.instance.costumes.find(costumeName)?.let { costume ->
                zOrder = costume.zOrder

                // Set the default size for NinePatches and tiled Poses.
                val pose = costume.pose()
                if (pose == null) {
                    val ninePatch = costume.chooseNinePatch(costume.initialEventName)
                    if (ninePatch != null) {
                        size.x = ninePatch.pose.rect.width.toDouble()
                        size.y = ninePatch.pose.rect.height.toDouble()
                        sizeAlignment.x = ninePatch.pose.offsetX / size.x
                        sizeAlignment.y = ninePatch.pose.offsetY / size.y
                    }
                } else {
                    direction.radians = pose.direction.radians

                    if (pose.tiled) {
                        size.x = pose.rect.width.toDouble()
                        size.y = pose.rect.height.toDouble()
                        sizeAlignment.x = pose.offsetX / size.x
                        sizeAlignment.y = pose.offsetY / size.y
                    }
                }
            }
        }

    override var x: Double = 0.0
    override var y: Double = 0.0
    override var zOrder: Double = 0.0

    var viewAlignmentX: ActorXAlignment = ActorXAlignment.LEFT
    var viewAlignmentY: ActorYAlignment = ActorYAlignment.BOTTOM

    /**
     * Used by SceneEditor in conjunction with StageConstraint. This is where the actor was dragged to, but [x],[y]
     * are the final position of the actor determined by the StageConstraint. When using NoStageConstraint,
     * draggedX,draggedY will be the same as x,y.
     */
    var draggedX: Double = 0.0

    var draggedY: Double = 0.0

    val direction = Angle()

    var scale = Vector2d(1.0, 1.0)

    /**
     * For resizable actors only (nine patch and tiled poses)
     */
    var size = Vector2d(1.0, 1.0)

    /**
     * For NinePatch only and Tiled?
     */
    var sizeAlignment = Vector2d(0.5, 0.5)

    val attributes = Resources.instance.createAttributes()

    val editorPose: Pose? by lazy { Resources.instance.costumes.find(costumeName)?.editorPose() }

    val pose: Pose? by lazy {
        val costume = Resources.instance.costumes.find(costumeName)
        costume?.choosePose(costume.initialEventName)
    }

    /**
     * The text style as defined in the Costume
     */
    val costumeTextStyle: TextStyle? by lazy {
        val costume = Resources.instance.costumes.find(costumeName)
        costume?.chooseTextStyle(costume.initialEventName)
    }

    /**
     * The text style, which can override the stlye from the costume.
     */
    val textStyle: TextStyle? by lazy {
        costumeTextStyle?.copy()
    }

    val ninePatch: NinePatch? by lazy {
        val costume = Resources.instance.costumes.find(costumeName)
        costume?.chooseNinePatch(costume.initialEventName)
    }

    var text: String = ""

    val displayText
        get() = if (text.isBlank()) "<no text>" else text

    init {
        if (isDesigning) {
            val costume = Resources.instance.costumes.find(costumeName)
            zOrder = costume?.zOrder ?: 0.0
        }
    }

    fun costume(): Costume? = Resources.instance.costumes.find(costumeName)

    fun isSizable(): Boolean {
        val costume = costume()
        if (costume?.chooseNinePatch(costume.initialEventName) != null) return true

        val pose = costume?.choosePose(costume.initialEventName)
        return pose?.tiled == true
    }

    fun isScalable(): Boolean {
        return costume()?.canScale == true
    }

    fun isNinePatch(): Boolean {
        val costume = costume()
        return costume?.chooseNinePatch(costume.initialEventName) != null
    }

    fun isText(): Boolean {
        return costumeTextStyle != null
    }

    fun createActor(): Actor? {
        val costume = costume()
        if (costume == null) {
            System.err.println("ERROR. Costume $costumeName not found in resources.")
            return null
        }
        val actor = costume.createActor(text)

        actor.x = x
        actor.y = y
        actor.zOrder = zOrder
        actor.direction.degrees = direction.degrees
        actor.scale = scale

        actor.viewAlignmentX = viewAlignmentX
        actor.viewAlignmentY = viewAlignmentY

        val appearance = actor.appearance
        if (appearance is ResizeAppearance) {
            actor.resize(size.x, size.y)
            appearance.sizeAlignment.set(sizeAlignment)
        } else if (appearance is TextAppearance) {
            textStyle?.let { appearance.textStyle = it }
        }

        actor.role?.let { attributes.applyToObject(it) }
        return actor
    }

    private fun updateAttributesMetaData() {
        val roleString = Resources.instance.costumes.find(costumeName)?.roleString
        if (roleString != null && roleString.isNotBlank()) {
            attributes.updateAttributesMetaData(roleString)
        }
    }

    override fun toString() = "ActorResource $costumeName @ $x , $y direction=$direction.degrees"
}

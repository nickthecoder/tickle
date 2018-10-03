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
import javafx.geometry.Side
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.TabPane
import javafx.stage.Modality
import javafx.stage.Stage
import org.jbox2d.dynamics.BodyType
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.DesignJsonScene
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.editor.util.*
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.physics.*
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import uk.co.nickthecoder.tickle.sound.Sound

class CostumeTab(val name: String, val costume: Costume)

    : EditTab(name, costume, graphicName = "costume.png") {

    val detailsTask = CostumeDetailsTask()
    val detailsForm = TaskForm(detailsTask)

    val eventsTask = CostumeEventsTask()
    val eventsForm = TaskForm(eventsTask)

    val physicsTask = PhysicsTask()
    val physicsForm = TaskForm(physicsTask)

    val minorTabs = MyTabPane<MyTab>()

    val detailsTab = MyTab("Details", detailsForm.build())
    val eventsTab = MyTab("Events", eventsForm.build())
    val physicsTab = MyTab("Physics", physicsForm.build())

    val posesButton = MenuButton("Poses")

    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(detailsTab)
        minorTabs.add(eventsTab)
        if (Resources.instance.gameInfo.physicsEngine) {
            minorTabs.add(physicsTab)
        }

        borderPane.center = minorTabs

        addCopyButton(costume, ResourceType.COSTUME) { newName, newCostume ->
            Resources.instance.costumes.add(newName, newCostume)
            newCostume.costumeGroup?.add(newName, newCostume)
        }
        buildPosesButton()

        detailsTask.taskD.root.listen { needsSaving = true }
        eventsTask.taskD.root.listen { needsSaving = true }
        physicsTask.taskD.root.listen { needsSaving = true }
    }

    private fun buildPosesButton() {
        val poses = mutableSetOf<Pose>()
        costume.events.values.forEach { event ->
            poses.addAll(event.poses)
            poses.addAll(event.ninePatches.map { it.pose })
        }

        if (poses.isEmpty()) {
            leftButtons.children.remove(posesButton)
        } else {
            posesButton.items.clear()
            poses.forEach { pose ->
                Resources.instance.poses.findName(pose)?.let { name ->
                    val menuItem = MenuItem(name)
                    posesButton.items.add(menuItem)
                    menuItem.onAction = EventHandler { MainWindow.instance.openTab(name, pose) }
                }
            }
            if (!leftButtons.children.contains(posesButton)) {
                leftButtons.children.add(posesButton)
            }
        }
    }

    override fun justSave(): Boolean {
        if (detailsForm.check()) {
            if (eventsForm.check()) {
                if (physicsForm.check()) {
                    detailsTask.run()
                    eventsTask.run()
                    physicsTask.run()
                    return true
                } else {
                    physicsTab.isSelected = true
                }
            } else {
                eventsTab.isSelected = true
            }
        } else {
            detailsTab.isSelected = true
        }
        return false
    }

    inner class CostumeDetailsTask : AbstractTask() {

        private val nameP = StringParameter("name", value = name)

        private val roleClassP = ClassParameter("role", Role::class.java)

        private val canRotateP = BooleanParameter("canRotate")

        private val zOrderP = DoubleParameter("zOrder")

        private val costumeGroupP = CostumeGroupParameter { chooseCostumeGroup(it) }

        private val showInSceneEditorP = BooleanParameter("showInSceneEditor", value = costume.showInSceneEditor)

        private val infoP = InformationParameter("info",
                information = "The role has no fields with the '@CostumeAttribute' annotation, and therefore, this costume has no attributes.")
        private val attributesP = SimpleGroupParameter("attributes")

        override val taskD = TaskDescription("costumeDetails")
                .addParameters(nameP, roleClassP, canRotateP, zOrderP, costumeGroupP, showInSceneEditorP, attributesP)

        init {
            nameP.value = name
            roleClassP.classValue = costume.roleClass()
            canRotateP.value = costume.canRotate
            zOrderP.value = costume.zOrder
            costumeGroupP.costumeP.value = costume.costumeGroup

            updateAttributes()
            roleClassP.listen {
                updateAttributes()
            }
        }

        override fun run() {
            costume.roleString = if (roleClassP.classValue == null) "" else ScriptManager.nameForClass(roleClassP.classValue!!)
            costume.canRotate = canRotateP.value == true
            costume.zOrder = zOrderP.value!!
            costume.showInSceneEditor = showInSceneEditorP.value == true

            if (costume.costumeGroup != costumeGroupP.costumeP.value) {
                costume.costumeGroup?.remove(name)
                costume.costumeGroup = costumeGroupP.costumeP.value
                costume.costumeGroup?.add(nameP.value, costume)
                if (costume.costumeGroup == null) {
                    Resources.instance.fireAdded(costume, name)
                }
            }
            if (nameP.value != name) {
                Resources.instance.costumes.rename(name, nameP.value)
                TaskPrompter(RenameCostumeTask(name, nameP.value)).placeOnStage(Stage())
            }

        }


        fun chooseCostumeGroup(groupName: String) {
            costumeGroupP.costumeP.value = Resources.instance.costumeGroups.find(groupName)
        }

        fun updateAttributes() {

            roleClassP.classValue?.let {
                costume.attributes.updateAttributesMetaData(it)
            }

            attributesP.children.toList().forEach {
                attributesP.remove(it)
            }
            attributesP.hidden = roleClassP.classValue == null


            costume.attributes.data().sortedBy { it.order }.forEach { data ->
                data as DesignAttributeData

                data.costumeParameter?.let { it ->
                    val parameter = it.copyBounded()
                    attributesP.add(parameter)
                    try {
                        parameter.stringValue = data.value ?: ""
                    } catch (e: Exception) {
                        // Do nothing
                    }
                }
            }

            if (attributesP.children.size == 0) {
                attributesP.add(infoP)
            }
        }

    }

    inner class CostumeEventsTask : AbstractTask() {

        val initialEventP = StringParameter("initialEvent", value = costume.initialEventName)

        val inheritEventsFromP = createCostumeParameter("inheritEventsFrom", required = false, value = costume.inheritEventsFrom)

        val eventsP = MultipleParameter("events") {
            EventParameter()
        }.asListDetail(isBoxed = true, allowReordering = false) { it.toString() }

        override val taskD = TaskDescription("costumeEvents")
                .addParameters(initialEventP, inheritEventsFromP, eventsP)

        init {
            costume.events.forEach { eventName, event ->
                event.poses.forEach { pose ->
                    val inner = eventsP.newValue()
                    inner.eventNameP.value = eventName
                    inner.poseP.value = pose
                    inner.typeP.value = inner.poseP
                }
                event.costumes.forEach { costume ->
                    val inner = eventsP.newValue()
                    inner.eventNameP.value = eventName
                    inner.costumeP.value = costume
                    inner.typeP.value = inner.costumeP
                }
                event.textStyles.forEach { textStyle ->
                    val inner = eventsP.newValue()
                    inner.eventNameP.value = eventName
                    inner.textStyleP.from(textStyle)
                    inner.typeP.value = inner.textStyleP
                }
                event.strings.forEach { str ->
                    val inner = eventsP.newValue()
                    inner.eventNameP.value = eventName
                    inner.stringP.value = str
                    inner.typeP.value = inner.stringP
                }
                event.sounds.forEach { sound ->
                    val inner = eventsP.newValue()
                    inner.eventNameP.value = eventName
                    inner.soundP.value = sound
                    inner.typeP.value = inner.soundP
                }
                event.ninePatches.forEach { ninePatch ->
                    val inner = eventsP.newValue()
                    inner.eventNameP.value = eventName
                    inner.ninePatchP.from(ninePatch)
                    inner.typeP.value = inner.ninePatchP
                }
            }
        }

        override fun run() {
            costume.initialEventName = initialEventP.value
            costume.inheritEventsFrom = inheritEventsFromP.value

            costume.events.clear()
            eventsP.innerParameters.forEach { inner ->
                when (inner.typeP.value) {
                    inner.poseP -> addPose(inner.eventNameP.value, inner.poseP.value!!)
                    inner.costumeP -> addCostume(inner.eventNameP.value, inner.costumeP.value!!)
                    inner.textStyleP -> addTextStyle(inner.eventNameP.value, inner.textStyleP.createTextStyle())
                    inner.stringP -> addString(inner.eventNameP.value, inner.stringP.value)
                    inner.soundP -> addSound(inner.eventNameP.value, inner.soundP.value!!)
                    inner.ninePatchP -> addNinePatch(inner.eventNameP.value, inner.ninePatchP.createNinePatch())
                }
            }
        }

        fun addPose(eventName: String, pose: Pose) {
            var event = costume.events[eventName]
            if (event == null) {
                event = CostumeEvent()
                costume.events[eventName] = event
            }
            event.poses.add(pose)
        }

        fun addCostume(eventName: String, cos: Costume) {
            var event = costume.events[eventName]
            if (event == null) {
                event = CostumeEvent()
                costume.events[eventName] = event
            }
            event.costumes.add(cos)
        }

        fun addTextStyle(eventName: String, textStyle: TextStyle) {
            var event = costume.events[eventName]
            if (event == null) {
                event = CostumeEvent()
                costume.events[eventName] = event
            }
            event.textStyles.add(textStyle)
        }

        fun addString(eventName: String, str: String) {
            var event = costume.events[eventName]
            if (event == null) {
                event = CostumeEvent()
                costume.events[eventName] = event
            }
            event.strings.add(str)
        }

        fun addSound(eventName: String, sound: Sound) {
            var event = costume.events[eventName]
            if (event == null) {
                event = CostumeEvent()
                costume.events[eventName] = event
            }
            event.sounds.add(sound)
        }

        fun addNinePatch(eventName: String, ninePatch: NinePatch) {
            var event = costume.events[eventName]
            if (event == null) {
                event = CostumeEvent()
                costume.events[eventName] = event
            }
            event.ninePatches.add(ninePatch)
        }
    }

    inner class EventParameter : MultipleGroupParameter("event") {

        val eventNameP = StringParameter("eventName")

        val poseP = createPoseParameter()

        val textStyleP = TextStyleParameter("style")

        val costumeP = createCostumeParameter()

        val stringP = StringParameter("string")

        val soundP = createSoundParameter()

        val ninePatchP = createNinePatchParameter()

        val typeP = OneOfParameter("type", value = null, choiceLabel = "Type")
                .addChoices(poseP, costumeP, textStyleP, stringP, soundP, ninePatchP)

        init {
            addParameters(eventNameP, typeP, poseP, ninePatchP, costumeP, textStyleP, stringP, soundP)
        }

        override fun toString(): String {
            val eventName = if (eventNameP.value.isBlank()) "<no name>" else eventNameP.value
            val type = typeP.value?.label ?: ""
            val dataName = when (typeP.value) {
                poseP -> Resources.instance.poses.findName(poseP.value)
                costumeP -> Resources.instance.costumes.findName(costumeP.value)
                textStyleP -> Resources.instance.fontResources.findName(textStyleP.fontP.value)
                stringP -> stringP.value
                soundP -> Resources.instance.sounds.findName(soundP.value)
                ninePatchP -> "Nine Patch (${Resources.instance.poses.findName(ninePatchP.poseP.value)})"
                else -> ""
            }
            return "$eventName ($type) $dataName"
        }
    }

    inner class PhysicsTask : AbstractTask() {

        val bodyTypeP = ChoiceParameter<BodyType?>("bodyType", required = false, value = costume.bodyDef?.type)
                .nullableEnumChoices(mixCase = true, nullLabel = "None")

        val linearDampingP = DoubleParameter("linearDamping", value = costume.bodyDef?.linearDamping ?: 0.0, minValue = 0.0, maxValue = 10.0)

        val angularDampingP = DoubleParameter("angularDamping", value = costume.bodyDef?.angularDamping ?: 0.0, minValue = 0.0, maxValue = 10.0)

        val fixedRotationP = BooleanParameter("fixedRotation", value = costume.bodyDef?.fixedRotation ?: false)

        val bulletP = BooleanParameter("bullet", value = costume.bodyDef?.bullet ?: false)

        val fixturesP = MultipleParameter("fixtures", minItems = 1) {
            FixtureParameter()
        }.asListDetail(isBoxed = true) { param ->
            param.toString()
        }

        override val taskD = TaskDescription("physics")
                .addParameters(bodyTypeP, linearDampingP, angularDampingP, fixedRotationP, bulletP, fixturesP)

        init {
            costume.bodyDef?.fixtureDefs?.forEach { fixtureDef ->
                val fixtureParameter = fixturesP.newValue()
                fixtureParameter.initParameters(fixtureDef)
            }
            updateHiddenFields()
            bodyTypeP.listen {
                updateHiddenFields()
            }
        }

        fun updateHiddenFields() {
            val static = bodyTypeP.value == BodyType.STATIC || bodyTypeP.value == null
            linearDampingP.hidden = static
            angularDampingP.hidden = static
            fixedRotationP.hidden = static
            bulletP.hidden = static

            fixturesP.hidden = !(bodyTypeP.value?.hasFixtures() ?: false)
        }

        override fun run() {
            if (bodyTypeP.value == null) {
                costume.bodyDef = null
            } else {
                val bodyDef = costume.bodyDef ?: TickleBodyDef()
                costume.bodyDef = bodyDef

                with(bodyDef) {
                    type = bodyTypeP.value!!
                    linearDamping = linearDampingP.value!!
                    angularDamping = angularDampingP.value!!
                    bullet = bulletP.value == true
                    fixedRotation = fixedRotationP.value!!
                }

                bodyDef.fixtureDefs.clear()
                fixturesP.innerParameters.forEach { fixtureParameter ->
                    bodyDef.fixtureDefs.add(fixtureParameter.createCostumeFixtureDef())
                }
            }
        }

        inner class FixtureParameter() : MultipleGroupParameter("fixture") {

            val densityP = FloatParameter("density", minValue = 0f, value = 1f)
            val frictionP = FloatParameter("friction", minValue = 0f, maxValue = 1f, value = 0f)
            val restitutionP = FloatParameter("restitution", minValue = 0f, maxValue = 1f, value = 1f)

            val isSensorP = BooleanParameter("isSensor", value = false)

            val circleCenterP = Vector2dParameter("circleCenter", label = "Center", showXY = false).asHorizontal()
            val circleRadiusP = DoubleParameter("circleRadius", label = "Radius")

            val circleP = SimpleGroupParameter("circle")
                    .addParameters(circleCenterP, circleRadiusP)

            val boxSizeP = Vector2dParameter("boxSize", showXY = false).asHorizontal()
            val boxCenterP = Vector2dParameter("boxCenter", label = "Center", showXY = false).asHorizontal()
            val boxAngleP = AngleParameter("boxAngle", label = "Angle")

            val boxP = SimpleGroupParameter("box")
                    .addParameters(boxSizeP, boxCenterP, boxAngleP)

            val polygonInfo = InformationParameter("polygonInfo", information = "Note. The polygon must be convex. Create more than one fixture to build concave objects.")
            val polygonPointsP = MultipleParameter("polygonPoints", minItems = 2) {
                Vector2dParameter("point").asHorizontal()
            }
            val polygonP = SimpleGroupParameter("polygon")
                    .addParameters(polygonInfo, polygonPointsP)

            val shapeP = OneOfParameter("shape", choiceLabel = "Type")
                    .addChoices(
                            "Circle" to circleP,
                            "Box" to boxP,
                            "Polygon" to polygonP)

            val shapeEditorButtonP = ButtonParameter("editShape", label = "", buttonText = "Edit Shape") { onEditShape() }

            val filterGroupP = createFilterGroupParameter()
            val filterCategoriesP = createFilterBitsParameter("categories", "I Am")
            val filterMaskP = createFilterBitsParameter("mask", "I Collide With")

            init {
                addParameters(densityP, frictionP, restitutionP, isSensorP, shapeP)
                (costume.pose() ?: costume.chooseNinePatch("default")?.pose)?.let {
                    addParameters(shapeEditorButtonP)

                    circleRadiusP.value = 15.0
                    boxSizeP.xP.value = 15.0
                    boxSizeP.yP.value = 15.0
                }

                addParameters(circleP, boxP, polygonP, filterGroupP, filterCategoriesP, filterMaskP)

                bodyTypeP.listen {
                    densityP.hidden = bodyTypeP.value != BodyType.DYNAMIC && bodyTypeP.value != BodyType.STATIC
                    frictionP.hidden = bodyTypeP.value != BodyType.DYNAMIC && bodyTypeP.value != BodyType.STATIC
                }
                // TODO Why are we firing a change event?
                bodyTypeP.parameterListeners.fireValueChanged(bodyTypeP, bodyTypeP.value)
            }

            fun initParameters(fixtureDef: TickleFixtureDef) {
                with(fixtureDef) {
                    densityP.value = density
                    frictionP.value = friction
                    restitutionP.value = restitution
                    isSensorP.value = isSensor
                    filterGroupP.value = filter.groupIndex
                    filterCategoriesP.value = filter.categoryBits
                    filterMaskP.value = filter.maskBits
                }
                with(fixtureDef.shapeDef) {
                    when (this) {
                        is CircleDef -> {
                            shapeP.value = circleP
                            circleCenterP.x = center.x
                            circleCenterP.y = center.y
                            circleRadiusP.value = radius
                        }
                        is BoxDef -> {
                            shapeP.value = boxP
                            boxSizeP.xP.value = width
                            boxSizeP.yP.value = height
                            boxCenterP.value = center
                            boxAngleP.value = angle
                        }
                        is PolygonDef -> {
                            shapeP.value = polygonP
                            polygonPointsP.clear()
                            points.forEach { point ->
                                polygonPointsP.addValue(point)
                            }
                        }
                    }
                }
            }

            override fun check() {
                super.check()

                // Check if the shape is concave.
                // Compute the dot product of the each line with the previous line's normal. For a convex shape
                // the dot products should all be positive or all be negative.
                // BTW, despite what people think, maths isn't my strong suit, so there may be a more efficient method.
                // Note. this doesn't detect "star" shapes, such as a witches pentagram, but it is unlikely somebody
                // would create such a shape! Summing the interior angles, and test if it is significantly over 2 PI
                // would fix that, but I can't be bothered. Sorry.
                var negativeCount = 0
                val points = polygonPointsP.innerParameters
                if (shapeP.value == polygonP) {
                    points.forEachIndexed { index, pointP ->
                        val point = pointP.value
                        val prev = points[if (index == 0) points.size - 1 else index - 1].value
                        val next = points[if (index == points.size - 1) 0 else index + 1].value
                        val line1 = Vector2d()
                        val line2 = Vector2d()
                        prev.sub(point, line1)
                        next.sub(point, line2)
                        val normal1 = Vector2d(line1.y, -line1.x)
                        val dotProduct = normal1.dot(line2)
                        if (dotProduct < 0.0) {
                            negativeCount++
                        }
                        // If any lines are straight the dot product will be zero. That is redundant point and will just a waste of CPU!
                        if (dotProduct == 0.0) {
                            throw ParameterException(shapeP, "Contains a redundant point #$index")
                        }
                    }
                }
                if (negativeCount > 0 && negativeCount != points.size) {
                    throw ParameterException(shapeP, "The shape is concave. Create 2 (or more) fixtures which are all convex.")
                }
            }

            fun createShapeDef(): ShapeDef? {
                try {
                    when (shapeP.value) {
                        circleP -> {
                            return CircleDef(Vector2d(circleCenterP.x!!, circleCenterP.y!!), circleRadiusP.value!!)
                        }
                        boxP -> {
                            return BoxDef(boxSizeP.xP.value!!, boxSizeP.yP.value!!, boxCenterP.value, boxAngleP.value)
                        }
                        polygonP -> {
                            return PolygonDef(polygonPointsP.value)
                        }
                        else -> {
                            return null
                        }
                    }
                } catch (e: KotlinNullPointerException) {
                    return null
                }
            }

            fun createCostumeFixtureDef(): TickleFixtureDef {
                val shapeDef = createShapeDef() ?: throw IllegalStateException("Not a valid shape")

                val fixtureDef = TickleFixtureDef(shapeDef)
                with(fixtureDef) {
                    density = densityP.value!!
                    friction = frictionP.value!!
                    restitution = restitutionP.value!!
                    isSensor = isSensorP.value == true
                    filter.groupIndex = filterGroupP.value!!
                    filter.categoryBits = filterCategoriesP.value
                    filter.maskBits = filterMaskP.value
                }

                return fixtureDef
            }

            fun onEditShape() {
                (costume.pose() ?: costume.chooseNinePatch("default")?.pose)?.let { pose ->
                    val task = ShapeEditorTask(pose, this)
                    val stage = Stage()
                    // always on top didn't work for me (on linux using open jdk and the open javafx.
                    // stage.isAlwaysOnTop = true
                    // So using a modal dialog instead (not what I wanted. grrr).
                    stage.initModality(Modality.APPLICATION_MODAL)
                    TaskPrompter(task, showCancel = false).placeOnStage(stage)

                    task.shapeEditorP.update(createShapeDef())
                    listen { task.shapeEditorP.update(createShapeDef()) }
                }
            }

            override fun toString(): String {
                return when (shapeP.value) {
                    circleP -> {
                        "Circle @ ${circleCenterP.x} , ${circleCenterP.y}"
                    }
                    boxP -> {
                        "Box @ ${boxCenterP.value.x} , ${boxCenterP.value.y}"
                    }
                    polygonP -> {
                        "Polygon (${polygonPointsP.value.size} points)"
                    }
                    else -> {
                        "Unknown"
                    }
                }
            }
        }

    }

}


fun createFilterGroupParameter(): ChoiceParameter<Int> {
    val choiceP = ChoiceParameter("collisionFilterGroup", value = 0)

    val filterGroup = Class.forName(Resources.instance.gameInfo.physicsInfo.filterGroupsString).newInstance() as FilterGroups
    filterGroup.values().forEach { name, value ->
        choiceP.addChoice(name, value, name)
    }
    if (filterGroup.values().size <= 1) {
        choiceP.hidden = true
    }
    return choiceP
}

fun createFilterBitsParameter(name: String, label: String) = FilterBitsParameter(name, label)

class FilterBitsParameter(
        name: String,
        label: String) : SimpleGroupParameter(name, label) {

    val filterMasks = Class.forName(Resources.instance.gameInfo.physicsInfo.filterBitsString).newInstance() as FilterBits

    init {
        filterMasks.values().forEach { maskName, _ ->
            val param = BooleanParameter("${name}_$maskName", label = maskName, value = false)
            addParameters(param)
        }
        asGrid(labelPosition = LabelPosition.LEFT, columns = filterMasks.columns(), isBoxed = true)
        if (filterMasks.values().isEmpty()) {
            hidden = true
        }
    }

    var value: Int
        get() {
            if (hidden) return 0xFFFF
            var value = 0
            filterMasks.values().forEach { maskName, bit ->
                val param = find("${name}_$maskName") as BooleanParameter
                if (param.value == true) {
                    value += bit
                }
            }
            return value
        }
        set(v) {
            filterMasks.values().forEach { maskName, bit ->
                val param = find("${name}_$maskName") as BooleanParameter
                param.value = (v and bit) != 0
            }
        }

}

class RenameCostumeTask(val oldCostumeName: String, val newCostumeName: String)
    : AbstractTask() {

    val informationP = InformationParameter("infoP", information = "To rename a costume requires loading and saving all scene.\nThis may take a few seconds.")

    override val taskD = TaskDescription("renameCostume")
            .addParameters(informationP)

    override fun run() {
        val fileLister = FileLister(extensions = listOf("scene"))
        fileLister.listFiles(Resources.instance.sceneDirectory).forEach { file ->
            val json = DesignJsonScene(file)
            val sceneResource = json.sceneResource
            var changed = false

            sceneResource.stageResources.forEach { _, stageResource ->
                stageResource.actorResources.forEach { actorResource ->
                    if (actorResource.costumeName == oldCostumeName) {
                        actorResource.costumeName = newCostumeName
                        changed = true
                    }
                }
            }
            if (changed) {
                json.save(file)
            }

        }
    }

}
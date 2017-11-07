package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Side
import javafx.scene.control.TabPane
import org.jbox2d.dynamics.BodyType
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.CostumeEvent
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.editor.util.*
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.physics.*
import uk.co.nickthecoder.tickle.resources.Resources
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

    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(detailsTab)
        minorTabs.add(eventsTab)
        if (Resources.instance.gameInfo.physicsEngine) {
            minorTabs.add(physicsTab)
        }

        borderPane.center = minorTabs

        addDeleteButton {
            costume.delete()
        }
        addCopyButton(costume, ResourceType.COSTUME) { newName, newCostume ->
            Resources.instance.costumes.add(newName, newCostume)
            newCostume.costumeGroup?.add(newName, newCostume)
        }
    }


    override fun save(): Boolean {
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

    inner class CostumeDetailsTask() : AbstractTask() {

        val nameP = StringParameter("name", value = name)

        val roleClassP = ChoiceParameter<Class<*>?>("role", required = false, value = null)

        val canRotateP = BooleanParameter("canRotate")

        val zOrderP = DoubleParameter("zOrder")

        val costumeGroupP = CostumeGroupParameter { chooseCostumeGroup(it) }

        val showInSceneEditorP = BooleanParameter("showInSceneEditor", value = costume.showInSceneEditor)

        val infoP = InformationParameter("info",
                information = "The role has no fields with the '@CostumeAttribute' annotation, and therefore, this costume has no attributes.")
        val attributesP = SimpleGroupParameter("attributes")

        override val taskD = TaskDescription("costumeDetails")
                .addParameters(nameP, roleClassP, canRotateP, zOrderP, costumeGroupP, showInSceneEditorP, attributesP)

        init {
            ClassLister.setNullableChoices(roleClassP, Role::class.java)

            nameP.value = name
            roleClassP.value = costume.roleClass()
            canRotateP.value = costume.canRotate
            zOrderP.value = costume.zOrder
            costumeGroupP.costumeP.value = costume.costumeGroup

            updateAttributes()
            roleClassP.listen {
                updateAttributes()
            }
        }


        override fun run() {
            if (nameP.value != name) {
                Resources.instance.costumes.rename(name, nameP.value)
            }

            costume.roleString = if (roleClassP.value == null) "" else roleClassP.value!!.name
            costume.canRotate = canRotateP.value == true
            costume.zOrder = zOrderP.value!!
            costume.showInSceneEditor = showInSceneEditorP.value == true

            if (costume.costumeGroup != costumeGroupP.costumeP.value) {
                costume.costumeGroup?.remove(name)
                costume.costumeGroup = costumeGroupP.costumeP.value
                costume.costumeGroup?.add(nameP.value, costume)
            }
        }

        fun chooseCostumeGroup(groupName: String) {
            costumeGroupP.costumeP.value = Resources.instance.costumeGroups.find(groupName)
        }

        fun updateAttributes() {

            roleClassP.value?.name?.let {
                costume.attributes.updateAttributesMetaData(it, true)
            }

            attributesP.children.toList().forEach {
                attributesP.remove(it)
            }
            attributesP.hidden = roleClassP.value == null


            costume.attributes.data().forEach { data ->
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
    }

    inner class EventParameter : MultipleGroupParameter("event") {

        val eventNameP = StringParameter("eventName")

        val poseP = createPoseParameter()
        val textStyleP = TextStyleParameter("style")
        val costumeP = createCostumeParameter()

        val stringP = StringParameter("string")

        val soundP = createSoundParameter()

        val typeP = OneOfParameter("type", value = poseP, choiceLabel = "Type")
                .addParameters(poseP, costumeP, textStyleP, stringP, soundP)

        init {
            addParameters(eventNameP, typeP)
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
                else -> ""
            }
            return "$eventName ($type) $dataName"
        }
    }

    inner class PhysicsTask : AbstractTask() {

        val bodyTypeP = ChoiceParameter("bodyType", required = false, value = costume.bodyDef?.type)
                .nullableEnumChoices(mixCase = true, nullLabel = "None")

        val linearDampingP = FloatParameter("linearDamping", value = costume.bodyDef?.linearDamping ?: 0f, minValue = 0f, maxValue = 1f)

        val angularDampingP = FloatParameter("angularDamping", value = costume.bodyDef?.angularDamping ?: 0f, minValue = 0f, maxValue = 1f)

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

            val circleRadiusP = DoubleParameter("circleRadius", label = "Radius")
            val circleCenterP = Vector2dParameter("circleCenter", label = "Center", showXY = false).asHorizontal()

            val circleP = SimpleGroupParameter("circle", label = "")
                    .addParameters(circleRadiusP, circleCenterP)
                    .asPlain()

            val boxSizeP = Vector2dParameter("boxSize", showXY = false).asHorizontal()
            val boxCenterP = Vector2dParameter("boxCenter", label = "Center", showXY = false).asHorizontal()
            val boxAngleP = AngleParameter("boxAngle", label = "Angle")
            val boxRoundedEndsP = BooleanParameter("roundedEnds", value = false)

            val boxP = SimpleGroupParameter("box", label = "")
                    .addParameters(boxSizeP, boxCenterP, boxAngleP, boxRoundedEndsP)
                    .asPlain()

            val shapeP = OneOfParameter("shape", choiceLabel = "Type")
                    .addParameters("Circle" to circleP, "Box" to boxP)

            var shapeEditorP: ShapeEditorParameter? = null

            val filterGroupP = createFilterGroupParameter()
            val filterCategoriesP = createFilterBitsParameter("categories", "I Am")
            val filterMaskP = createFilterBitsParameter("mask", "I Collide With")

            init {
                addParameters(densityP, frictionP, restitutionP, isSensorP, shapeP)
                costume.editorPose()?.let { pose ->
                    shapeEditorP = ShapeEditorParameter("shapeEditor", pose)
                    addParameters(shapeEditorP!!)
                }
                addParameters(filterGroupP, filterCategoriesP, filterMaskP)

                densityP.hidden = bodyTypeP.value != BodyType.DYNAMIC
                frictionP.hidden = bodyTypeP.value != BodyType.DYNAMIC
                bodyTypeP.listen {
                    densityP.hidden = bodyTypeP.value != BodyType.DYNAMIC
                    frictionP.hidden = bodyTypeP.value != BodyType.DYNAMIC
                }
                shapeP.listen {
                    shapeEditorP?.update(createShapeDef())
                }
                shapeEditorP?.update(createShapeDef())
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
                            circleCenterP.value = center
                            circleRadiusP.value = radius
                        }
                        is BoxDef -> {
                            shapeP.value = boxP
                            boxSizeP.xP.value = width
                            boxSizeP.yP.value = height
                            boxCenterP.value = center
                            boxAngleP.value = angle
                            boxRoundedEndsP.value = roundedEnds
                        }
                    }
                }
            }

            fun defaultSizes(pose: Pose): FixtureParameter {
                circleRadiusP.value = Math.min(pose.rect.width, pose.rect.height).toDouble()
                boxSizeP.xP.value = pose.rect.width.toDouble()
                boxSizeP.yP.value = pose.rect.height.toDouble()
                return this
            }

            fun createShapeDef(): ShapeDef? {
                try {
                    when (shapeP.value) {
                        circleP -> {
                            return CircleDef(circleCenterP.value, circleRadiusP.value!!)
                        }
                        boxP -> {
                            return BoxDef(boxSizeP.xP.value!!, boxSizeP.yP.value!!, boxCenterP.value, boxAngleP.value, roundedEnds = boxRoundedEndsP.value == true)
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
                val shapeDef = createShapeDef()
                if (shapeDef == null) {
                    throw IllegalStateException("Not a valid shape")
                }

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

            override fun toString(): String {
                return when (shapeP.value) {
                    circleP -> {
                        "Circle @ ${circleCenterP.value.x} , ${circleCenterP.value.y}"
                    }
                    boxP -> {
                        "Box @ ${boxCenterP.value.x} , ${boxCenterP.value.y}"
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

package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Side
import javafx.scene.control.TabPane
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
import uk.co.nickthecoder.tickle.resources.Resources

class CostumeTab(val name: String, val costume: Costume)

    : EditTab(name, costume, graphicName = "costume.png") {

    val detailsTask = CostumeDetailsTask()
    val detailsForm = TaskForm(detailsTask)

    val eventsTask = CostumeEventsTask()
    val eventsForm = TaskForm(eventsTask)

    val minorTabs = MyTabPane<MyTab>()

    val detailsTab = MyTab("Details", detailsForm.build())
    val eventsTab = MyTab("Events", eventsForm.build())

    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(detailsTab)
        minorTabs.add(eventsTab)

        borderPane.center = minorTabs

        addDeleteButton {
            costume.costumeGroup?.remove(name)
            Resources.instance.costumes.remove(name)
        }
    }

    override fun save(): Boolean {
        if (detailsForm.check()) {
            if (eventsForm.check()) {
                detailsTask.run()
                eventsTask.run()
                return true
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

        val roleClassP = ChoiceParameter<Class<*>?>("class", required = false, value = null)

        val canRotateP = BooleanParameter("canRotate")

        val zOrderP = DoubleParameter("zOrder")

        val costumeGroupP = CostumeGroupParameter { chooseCostumeGroup(it) }

        val infoP = InformationParameter("info",
                information = "The role has no fields with the '@CostumeAttribute' annotation, and therefore, this costume has no attributes.")
        val attributesP = SimpleGroupParameter("attributes")

        override val taskD = TaskDescription("costumeDetails")
                .addParameters(nameP, roleClassP, canRotateP, zOrderP, costumeGroupP, attributesP)

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

    inner class CostumeEventsTask() : AbstractTask() {

        val initialEventP = StringParameter("initialEvent", value = costume.initialEventName)

        val eventsP = MultipleParameter("events") {
            EventParameter()
        }.asListDetail(allowReordering = false) { it.toString() }

        override val taskD = TaskDescription("costumeEvents")
                .addParameters(initialEventP, eventsP)

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
            }
        }

        override fun run() {
            costume.initialEventName = initialEventP.value

            costume.events.clear()
            eventsP.innerParameters.forEach { inner ->
                when (inner.typeP.value) {
                    inner.poseP -> addPose(inner.eventNameP.value, inner.poseP.value!!)
                    inner.costumeP -> addCostume(inner.eventNameP.value, inner.costumeP.value!!)
                    inner.textStyleP -> addTextStyle(inner.eventNameP.value, inner.textStyleP.createTextStyle())
                    inner.stringP -> addString(inner.eventNameP.value, inner.stringP.value)
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
    }

    inner class EventParameter() : MultipleGroupParameter("event") {

        val eventNameP = StringParameter("eventName")

        val poseP = createPoseParameter()
        val textStyleP = TextStyleParameter("style")
        val costumeP = createCostumeParameter()

        val stringP = StringParameter("string")

        val typeP = OneOfParameter("type", value = poseP, choiceLabel = "Type")
                .addParameters(poseP, costumeP, textStyleP, stringP)

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
                else -> ""
            }
            return "$eventName ($type) $dataName"
        }
    }

}

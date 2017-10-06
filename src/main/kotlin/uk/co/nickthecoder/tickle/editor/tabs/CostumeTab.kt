package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Side
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.createPoseParameter
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.util.CostumeAttribute

class CostumeTab(val name: String, val costume: Costume)

    : EditTab("Costume", name, costume, graphicName = "costume.png") {

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

        addDeleteButton { Resources.instance.deleteCostume(name) }
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

        val infoP = InformationParameter("info",
                information = "The role has no fields with the '@CostumeAttribute' annotation, and therefore, this costume has no attributes.")
        val attributesP = SimpleGroupParameter("attributes")

        override val taskD = TaskDescription("costumeDetails")
                .addParameters(nameP, roleClassP, canRotateP, attributesP)

        init {
            ClassLister.setNullableChoices(roleClassP, Role::class.java)

            nameP.value = name
            roleClassP.value = if (costume.roleString.isBlank()) null else Class.forName(costume.roleString)
            canRotateP.value = costume.canRotate

            updateAttributes()
            roleClassP.listen {
                updateAttributes()
            }
        }


        override fun run() {
            if (nameP.value != name) {
                Resources.instance.renameCostume(name, nameP.value)
            }

            costume.roleString = if (roleClassP.value == null) "" else roleClassP.value!!.name
            costume.canRotate = canRotateP.value == true

            with(costume.attributes) {
                map.clear()
                attributesP.children.forEach { child ->
                    if (child is ValueParameter<*>) {
                        if (child.value != null) {
                            map[Attributes.attributeName(child)] = child.stringValue
                        }
                    }
                }
            }
        }

        fun updateAttributes() {
            // Scan the Role class for annotations, and generate parameters for them.
            attributesP.children.toList().forEach {
                attributesP.remove(it)
            }
            attributesP.hidden = roleClassP.value == null

            roleClassP.value?.let { roleClass ->
                Attributes.createParameters(roleClass, CostumeAttribute::class).forEach { parameter ->
                    attributesP.add(parameter)
                    val attributeName = Attributes.attributeName(parameter)
                    costume.attributes.map[attributeName]?.let { value ->
                        try {
                            parameter.stringValue = value
                        } catch (e: Exception) {
                            // Do nothing
                        }
                    }
                }
            }

            if (attributesP.children.size == 0) {
                attributesP.add(infoP)
            }
        }

    }

    inner class CostumeEventsTask() : AbstractTask() {

        val eventsP = MultipleParameter("events") {
            EventParameter()
        }.asListDetail(allowReordering = false) { it.toString() }

        override val taskD = TaskDescription("costumeEvents")
                .addParameters(eventsP)

        init {
            costume.events.forEach { eventName, event ->
                event.poses.forEach { pose ->
                    val inner = eventsP.newValue()
                    inner.eventNameP.value = eventName
                    inner.poseP.value = pose
                    inner.typeP.value = inner.poseP
                }
            }
        }

        override fun run() {
            costume.events.clear()
            eventsP.innerParameters.forEach { inner ->
                if (inner.typeP.value == inner.poseP) {
                    addPose(inner.eventNameP.value, inner.poseP.value!!)
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
    }

    inner class EventParameter() : MultipleGroupParameter("event") {

        val eventNameP = StringParameter("eventName")

        val poseP = createPoseParameter()

        val typeP = OneOfParameter("type", value = poseP, choiceLabel = "Type")
                .addParameters(poseP)

        init {
            addParameters(eventNameP, typeP)
        }

        override fun toString(): String {
            val eventName = if (eventNameP.value.isBlank()) "<no name>" else eventNameP.value
            val type = typeP.value?.label ?: ""
            val dataName = when (typeP.value) {
                poseP -> Resources.instance.findPoseName(poseP.value)
                else -> ""
            }
            return "$eventName ($type) $dataName"
        }
    }

}

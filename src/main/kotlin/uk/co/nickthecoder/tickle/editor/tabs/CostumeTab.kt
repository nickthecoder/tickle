package uk.co.nickthecoder.tickle.editor.tabs

import javafx.geometry.Side
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.tickle.Attributes
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.util.CostumeAttribute

class CostumeTab(val name: String, val costume: Costume)

    : EditTab("Costume", name, costume) {

    val detailsTask = CostumeDetailsTask()
    val detailsForm = TaskForm(detailsTask)

    val minorTabs = MyTabPane<MyTab>()

    val detailsTab = MyTab("Details", detailsForm.build())

    init {
        minorTabs.side = Side.BOTTOM
        minorTabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        minorTabs.add(detailsTab)
        borderPane.center = minorTabs

        addDeleteButton { Resources.instance.deleteCostume(name) }
    }

    override fun save(): Boolean {
        if (detailsForm.check()) {
            detailsTask.run()
            return true
        }
        return false
    }

    inner class CostumeDetailsTask() : AbstractTask() {

        val nameP = StringParameter("name", value = name)

        val roleClassP = ChoiceParameter<Class<*>?>("class", required = false, value = null)

        val infoP = InformationParameter("info",
                information = "The role has no fields with the '@CostumeAttribute' annotation, and therefore, this costume has no attributes.")
        val attributesP = SimpleGroupParameter("attributes")

        override val taskD = TaskDescription("costumeDetails")
                .addParameters(nameP, roleClassP, attributesP)

        init {
            ClassLister.setNullableChoices(roleClassP, Role::class.java)

            nameP.value = name
            roleClassP.value = if (costume.roleString.isBlank()) null else Class.forName(costume.roleString)

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
}

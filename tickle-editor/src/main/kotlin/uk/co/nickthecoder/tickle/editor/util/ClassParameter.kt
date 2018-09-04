package uk.co.nickthecoder.tickle.editor.util

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.scripts.ScriptManager

class ClassParameter(
        name: String,
        val type: Class<*>,
        label: String = name.uncamel(),
        required: Boolean = false,
        value: Class<*>? = null)

    : SimpleGroupParameter(name, label = label) {

    private val classP = GroupedChoiceParameter<Class<*>?>("${name}Class", required = required, value = null, allowSingleItemSubMenus = true)

    private val newButtonP = ButtonParameter("${name}New", buttonText = "New ${type.simpleName}") { newScript() }

    var value: Class<*>?
        get() = classP.value
        set(value) {
            classP.value = value
        }

    init {
        classP.value = value
        addParameters(classP, newButtonP)
        asHorizontal(labelPosition = LabelPosition.NONE)

        newButtonP.hidden = ScriptManager.languages().isEmpty()
        ClassLister.setNullableChoices(classP, type)
    }

    private fun newScript() {
        val task = NewResourceTask(resourceType = ResourceType.SCRIPT, newScriptType = type)
        task.taskRunner.listen { cancelled ->
            if (!cancelled) {
                try {
                    ClassLister.setNullableChoices(classP, type)
                    classP.value = ScriptManager.classForName(task.nameP.value)
                } catch (e: ClassNotFoundException) {
                }
            }
        }
        TaskPrompter(task).placeOnStage(Stage())
    }

    override fun copy(): ClassParameter {
        return ClassParameter(name, type, label, required = classP.required, value = value)
    }

}

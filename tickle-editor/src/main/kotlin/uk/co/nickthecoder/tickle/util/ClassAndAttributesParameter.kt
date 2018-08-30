package uk.co.nickthecoder.tickle.util

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.editor.util.DesignAttributeData
import uk.co.nickthecoder.tickle.editor.util.DesignAttributes

class ClassAndAttributesParameter(name: String, klass: Class<*>)

    : SimpleGroupParameter(name) {

    val classP = GroupedChoiceParameter(name + "_class", value = klass)
    val attributesP = ButtonParameter(name + "_attributes", buttonText = "Attributes") { editAttributes() }

    var attributes: DesignAttributes? = null
        set(v) {
            field = v
            v?.let { attributes ->
                attributes.updateAttributesMetaData(classP.value!!.name)
                attributesP.hidden = attributes.data().firstOrNull { (it as DesignAttributeData).parameter != null } == null
            }
        }

    init {
        addParameters(classP, attributesP)
        attributesP.hidden = true
        asHorizontal(labelPosition = LabelPosition.NONE)
        ClassLister.setChoices(classP, klass)
    }

    fun editAttributes() {
        val task = AttributesTask(attributes!!)
        val prompter = TaskPrompter(task)
        prompter.placeOnStage(Stage())
    }
}

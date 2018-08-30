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

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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.EditorPreferences
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.resources.Resources

class EditorPreferencesTab

    : EditTaskTab(
        EditorPreferencesTask(Resources.instance.preferences),
        dataName = "Editor Preferences",
        data = Resources.instance.preferences,
        graphicName = "preferences.png") {

}

class EditorPreferencesTask(val editorPreferences: EditorPreferences) : AbstractTask() {

    val treeThumbnailSizeP = IntParameter("treeThumbnailSize", value = editorPreferences.treeThumnailSize)

    val costumePickerSizeP = IntParameter("costumePickerThumbnailSize", value = editorPreferences.costumePickerThumbnailSize)

    val packageInfoP = InformationParameter("packageInfo", information = "The top level packages used by your game. This is used to scan the code for Producer, Director and Role classes.")
    val packagesP = MultipleParameter("packages", label = "Packages", value = editorPreferences.packages.toMutableList()) {
        StringParameter("package")
    }
    val packagesGroupP = SimpleGroupParameter("packagesGroup", label = "Packages")
            .addParameters(packageInfoP, packagesP)

    val outputFormatP = ChoiceParameter<EditorPreferences.JsonFormat>("outputFormat", value = editorPreferences.outputFormat)
            .enumChoices(true)

    override val taskD = TaskDescription("editGameInfo")
            .addParameters(treeThumbnailSizeP, costumePickerSizeP, packagesGroupP, outputFormatP)

    override fun run() {
        ClassLister.packages(packagesP.value)

        with(editorPreferences) {
            packages.clear()
            packages.addAll(packagesP.value)
            treeThumnailSize = treeThumbnailSizeP.value!!
            costumePickerThumbnailSize = costumePickerSizeP.value!!
            outputFormat = outputFormatP.value!!
        }
    }

}

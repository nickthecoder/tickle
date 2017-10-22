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

    val packageInfoP = InformationParameter("packageInfo", information = "The top level packages used by your game. This is used to scan the code for Producer, Director and Role classes.")
    val packagesP = MultipleParameter("packages", label = "Packages", value = editorPreferences.packages.toMutableList()) {
        StringParameter("package")
    }
    val packagesGroupP = SimpleGroupParameter("packagesGroup", label = "Packages")
            .addParameters(packageInfoP, packagesP)

    val outputFormatP = ChoiceParameter<EditorPreferences.JsonFormat>("outputFormat", value = editorPreferences.outputFormat)
            .enumChoices(true)

    override val taskD = TaskDescription("editGameInfo")
            .addParameters(packagesGroupP, outputFormatP)

    override fun run() {
        ClassLister.packages(packagesP.value)

        with(editorPreferences) {
            packages.clear()
            packages.addAll(packagesP.value)
            outputFormat = outputFormatP.value!!
        }
    }

}

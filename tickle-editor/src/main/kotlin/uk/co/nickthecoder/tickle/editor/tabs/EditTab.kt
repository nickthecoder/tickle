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

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.gui.defaultWhileFocusWithin
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.editor.EditorActions
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.DesignResources
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.editor.util.deletePrompted
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.ResourcesListener
import uk.co.nickthecoder.tickle.util.Copyable
import uk.co.nickthecoder.tickle.util.Deletable


abstract class EditTab(
        dataName: String,
        data: Any,
        graphicName: String? = null)

    : EditorTab(dataName, data), ResourcesListener {

    protected val borderPane = BorderPane()

    val leftButtons = FlowPane()

    val rightButtons = FlowPane()

    protected val buttons = HBox()

    protected val okButton = Button("Ok")

    protected val applyButton = Button("Apply")

    protected val cancelButton = Button("Cancel")

    private val shortcuts = ShortcutHelper("Edit Tab", MainWindow.instance.borderPane)

    var needsSaving = false
        set(v) {
            okButton.isDisable = !v
            applyButton.isDisable = !v
            cancelButton.text = if (v) "Cancel" else "Close"
            field = v
        }

    init {
        okButton.isDisable = true
        applyButton.isDisable = true

        graphicName?.let { graphic = ImageView(EditorAction.imageResource(graphicName)) }

        content = borderPane

        with(borderPane) {
            styleClass.add("prompt")
            bottom = buttons
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            // isCancelButton = true
        }

        with(applyButton) {
            onAction = EventHandler { onApply() }
        }

        with(okButton) {
            onAction = EventHandler { onOk() }
            defaultWhileFocusWithin(borderPane)
        }


        with(leftButtons) {
            styleClass.addAll("buttons", "left")
        }

        with(rightButtons) {
            children.addAll(okButton, applyButton, cancelButton)
            styleClass.add("buttons")
        }

        with(buttons) {
            children.addAll(leftButtons, rightButtons)
        }

        with(shortcuts) {
            add(EditorActions.SAVE) { onApply() }
        }

        if (data is Deletable) {
            addDeleteButton(data)
        }

        Resources.instance.listeners.add(this)
    }


    override fun resourceRemoved(resource: Any, name: String) {
        if (resource === data) {
            close()
        }
    }

    override fun removed() {
        Resources.instance.listeners.remove(this)
    }

    protected fun addDeleteButton(deletable: Deletable) {
        val button = Button("Delete")

        button.setOnAction { deletable.deletePrompted(dataName) }
        leftButtons.children.add(button)
    }

    inline fun <reified T : Copyable<*>> addCopyButton(resource: Copyable<T>, resourceType: ResourceType, noinline action: (String, T) -> Unit) {
        val button = Button("Copy")

        button.setOnAction {
            val prompter = TaskPrompter(CopyResourceTask<T>(resource, resourceType) { newName, newResource ->
                action(newName, newResource)
                MainWindow.instance.openTab(newName, newResource)
            })
            prompter.placeOnStage(Stage())
        }
        leftButtons.children.add(button)
    }

    abstract fun save(): Boolean

    protected fun onCancel() {
        close()
    }

    protected fun onApply() {
        if (save()) {
            needsSaving = false
            Resources.instance.fireChanged(data)
            (Resources.instance as DesignResources).save()
        }
    }

    protected fun onOk() {
        onApply()
        close()
    }

    class CopyResourceTask<T>(val resource: Copyable<T>, resourceType: ResourceType, val action: (String, T) -> Unit)

        : AbstractTask() {

        val newNameP = StringParameter("newName", required = true)

        override val taskD = TaskDescription("copy${resourceType.label}")
                .addParameters(newNameP)

        override fun run() {
            Platform.runLater {
                val copy = resource.copy()
                action(newNameP.value, copy)

            }
        }
    }
}

package uk.co.nickthecoder.tickle.editor.tabs

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane

abstract class EditTab(name: String, data: Any)

    : EditorTab(name, data) {

    val borderPane = BorderPane()

    private val buttons = FlowPane()

    val okButton = Button("Ok")

    val applyButton = Button("Apply")

    val cancelButton = Button("Cancel")


    init {
        content = borderPane

        with(borderPane) {
            styleClass.add("prompt")
            bottom = buttons
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            isCancelButton = true
        }

        with(applyButton) {
            onAction = EventHandler { onApply() }
            isCancelButton = true
        }

        with(okButton) {
            onAction = EventHandler { onOk() }
            isDefaultButton = true
        }


        with(buttons) {
            children.addAll(okButton, applyButton, cancelButton)
            styleClass.add("buttons")
        }
    }

    abstract fun save(): Boolean

    protected fun onCancel() {
        close()
    }

    protected fun onApply() {
        save()
    }

    protected fun onOk() {
        if (save()) {
            close()
        }
    }
}

package uk.co.nickthecoder.tickle.editor.tabs

import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.ResourcesListener


abstract class EditTab(
        dataType: String,
        dataName: String,
        data: Any)

    : EditorTab(dataType, dataName, data), ResourcesListener {

    protected val borderPane = BorderPane()

    protected val leftButtons = FlowPane()

    protected val rightButtons = FlowPane()

    protected val buttons = HBox()

    protected val okButton = Button("Ok")

    protected val applyButton = Button("Apply")

    protected val cancelButton = Button("Cancel")

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


        with(leftButtons) {
            children.addAll(Label("$dataType : '$dataName'"))
            styleClass.addAll("buttons", "left")
        }

        with(rightButtons) {
            children.addAll(okButton, applyButton, cancelButton)
            styleClass.add("buttons")
        }

        with(buttons) {
            children.addAll(leftButtons, rightButtons)
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

    fun addDeleteButton(action: () -> Unit) {
        val button = Button("Delete")

        button.setOnAction {
            val alert = Alert(Alert.AlertType.CONFIRMATION, "Delete $dataType '$dataName' ?", ButtonType.YES, ButtonType.NO)
            alert.showAndWait()

            if (alert.result == ButtonType.YES) {
                action()
            }
        }
        leftButtons.children.add(button)
    }

    abstract fun save(): Boolean

    protected fun onCancel() {
        close()
    }

    protected fun onApply() {
        if (save()) {
            Resources.instance.fireChanged(data)
        }
    }

    protected fun onOk() {
        if (save()) {
            Resources.instance.fireChanged(data)
            close()
        }
    }
}

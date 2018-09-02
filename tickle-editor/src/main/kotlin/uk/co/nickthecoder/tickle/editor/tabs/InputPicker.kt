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
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.tickle.events.*

class InputPicker(val callback: (Input) -> Unit) {

    val message = Label("Press a key, click a mouse button here or press a button or thumb-pad on a game controller\n\nNote. Shift and Control keys won't be detected correctly. Sorry.")

    val borderPane = BorderPane()

    val cancelButton = Button("Cancel")

    val buttons = FlowPane()

    val scene = Scene(borderPane, 400.0, 180.0)

    var finish = false

    val stage = Stage()

    init {
        buttons.children.addAll(cancelButton)
        buttons.styleClass.add("buttons")

        message.style = "-fx-padding: 20px;"
        message.isWrapText = true

        borderPane.top = message
        borderPane.bottom = buttons
        cancelButton.onAction = EventHandler { stage.close() }
    }

    fun show() {
        ParaTask.style(scene)

        borderPane.onKeyPressed = EventHandler { event ->
            println("Key ${event.code.getName()}")
            sendBack(KeyInput(Key.forLabel(event.code.getName()), ButtonState.PRESSED))
        }

        borderPane.onMousePressed = EventHandler { event ->
            println("Mouse Button ${event.button}")
            sendBack(MouseInput(event.button.ordinal - 1, ButtonState.PRESSED))
        }

        stage.setOnHiding {
            finish = true
        }

        val thread = Thread() {
            scanJoysticks()
        }

        thread.isDaemon = true
        thread.start()

        stage.scene = scene
        stage.show()
    }

    fun scanJoysticks() {
        while (!finish) {
            for (joystickID in 0..Joystick.count) {
                if (Joystick.isPresent(joystickID)) {
                    JoystickButton.values().forEach { joystickButton ->
                        if (JoystickButtonInput(joystickID, joystickButton).isPressed()) {
                            println("Joystick #$joystickID Button $joystickButton")
                            sendBack(JoystickButtonInput(joystickID, joystickButton))
                        }
                    }
                    JoystickAxis.values().forEach { joystickAxis ->
                        val value = JoystickAxisInput(joystickID, joystickAxis).value()
                        if (value > 0.1 || value < -0.1) {
                            println("Joystick #$joystickID Axis $joystickAxis")
                            sendBack(JoystickAxisInput(joystickID, joystickAxis, positive = value > 0f))
                        }
                    }
                }
            }
            Thread.sleep(500)
        }
    }

    fun sendBack(input: Input) {
        Platform.runLater {
            callback(input)
            stage.close()
        }
    }

}

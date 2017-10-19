package uk.co.nickthecoder.tickle.editor.tabs

import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.resources.Resources

class InputTab(name: String, input: CompoundInput)
    : EditTaskTab(InputTask(name, input), name, input, graphicName = "input.png") {

    init {
        addDeleteButton { Resources.instance.inputs.remove(name) }
    }
}

class InputTask(val name: String, val compoundInput: CompoundInput) : AbstractTask() {

    val nameP = StringParameter("name", value = name)

    val inputsP = MultipleParameter("inputs", isBoxed = true) {
        InputParameter()
    }

    override val taskD = TaskDescription("editTexture")
            .addParameters(nameP, inputsP)

    init {
        inputsP.asListDetail(allowReordering = false) { it.toString() }

        compoundInput.inputs.forEach { input ->
            val inner = inputsP.newValue()
            inner.from(input)
        }
    }

    override fun customCheck() {
        val i = Resources.instance.inputs.find(nameP.value)
        if (i != null && i != compoundInput) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.inputs.rename(name, nameP.value)
        }
        compoundInput.inputs.clear()
        inputsP.innerParameters.forEach {
            it.toInput()?.let { compoundInput.add(it) }
        }
    }

}

class InputParameter : MultipleGroupParameter("input") {

    val pickInputP = ButtonParameter("pickInput", label = "", buttonText = "Click to Pick") { onPick() }

    val infoP = InformationParameter("info", information = "Or choose the input manually below...")

    // KEY

    val keyP = ChoiceParameter<Key?>("key", required = true, value = null).nullableEnumChoices(mixCase = true)

    val keyInfoP = InformationParameter("keyInfo", information = "The following field is only used when using KeyEvents, not when checking if a key is currently down.")

    val keyStateP = ChoiceParameter<ButtonState>("keyState", label = "State", value = ButtonState.PRESSED).enumChoices(mixCase = true)

    val keyInputP = SimpleGroupParameter("keyInput", label = "Keyboard")
            .addParameters(keyP, keyInfoP, keyStateP)

    // MOUSE

    val mouseButtonP = IntParameter("mouseButton", minValue = 0, value = 0)

    val mouseStateP = ChoiceParameter<ButtonState>("mouseState", label = "State", value = ButtonState.PRESSED).enumChoices(mixCase = true)

    val mouseInputP = SimpleGroupParameter("mouseInput", label = "Mouse")
            .addParameters(mouseButtonP, mouseStateP)

    // JOYSTICK BUTTON

    val buttonJoystickIDP = IntParameter("button_joystickID", label = "JoystickID", minValue = 0, maxValue = Joystick.count - 1, value = 0)

    val joystickButtonP = ChoiceParameter<JoystickButton>("joystickButton", value = JoystickButton.A).enumChoices(mixCase = true)

    val joystickButtonInputP = SimpleGroupParameter("joystickButton")
            .addParameters(buttonJoystickIDP, joystickButtonP)

    // JOYSTICK AXIS

    val axisJoystickIDP = IntParameter("axis_joystickID", label = "JoystickID", minValue = 0, maxValue = Joystick.count - 1, value = 0)

    val joystickAxisP = ChoiceParameter<JoystickAxis>("joystickAxis", value = JoystickAxis.LEFT_X).enumChoices(mixCase = true)

    val positiveP = BooleanParameter("positive", value = true)

    val thresholdP = DoubleParameter("threshold", value = 0.5, minValue = 0.0, maxValue = 1.0)

    val joystickAxisInputP = SimpleGroupParameter("joystickAxis")
            .addParameters(axisJoystickIDP, joystickAxisP, positiveP, thresholdP)

    // One Of ...

    val inputTypeP = OneOfParameter("inputType", label = " ", value = keyInputP, choiceLabel = "Input Type")
            .addParameters(keyInputP, mouseInputP, joystickButtonInputP, joystickAxisInputP).asPlain()

    private var keyPressHandler: EventHandler<KeyEvent>? = null

    init {
        addParameters(pickInputP, infoP, inputTypeP)
    }

    fun from(input: Input) {

        if (input is KeyInput) {
            inputTypeP.value = keyInputP
            keyP.value = input.key
            keyStateP.value = input.state

        } else if (input is MouseInput) {
            inputTypeP.value = mouseInputP
            mouseButtonP.value = input.mouseButton
            mouseStateP.value = input.state

        } else if (input is JoystickButtonInput) {
            inputTypeP.value = joystickButtonInputP
            buttonJoystickIDP.value = input.joystickID
            joystickButtonP.value = input.button

        } else if (input is JoystickAxisInput) {
            inputTypeP.value = joystickAxisInputP
            axisJoystickIDP.value = input.joystickID
            positiveP.value = input.positive
            thresholdP.value = input.threshold.toDouble()

        }
    }

    fun toInput(): Input? {

        if (inputTypeP.value == keyInputP) {
            return KeyInput(keyP.value!!, keyStateP.value!!)

        } else if (inputTypeP.value == mouseInputP) {
            return MouseInput(mouseButtonP.value!!, mouseStateP.value!!)

        } else if (inputTypeP.value == joystickButtonInputP) {
            return JoystickButtonInput(buttonJoystickIDP.value!!, joystickButtonP.value!!)

        } else if (inputTypeP.value == joystickAxisInputP) {
            return JoystickAxisInput(axisJoystickIDP.value!!, joystickAxisP.value!!, positiveP.value!!, thresholdP.value!!)
        }

        return null
    }


    override fun toString(): String {
        return if (inputTypeP.value == keyInputP) {
            "Key ${keyP.value ?: "<unknown>"}"

        } else if (inputTypeP.value == mouseInputP) {
            "Mouse button ${mouseButtonP.value ?: "<unknown>"}"

        } else if (inputTypeP.value == joystickButtonInputP) {
            "Joystick #${buttonJoystickIDP.value ?: "?"} ${joystickButtonP.value ?: "<unknown>"}"

        } else if (inputTypeP.value == joystickAxisInputP) {
            val plusMinus = if (positiveP.value == true) "+" else "-"
            "Axis #${buttonJoystickIDP.value ?: "?"} $plusMinus ${joystickAxisP.value ?: "<unknown>"}"

        } else {
            "<new>"
        }
    }

    fun onPick() {
        InputPicker { input ->
            from(input)
        }.show()
    }

}

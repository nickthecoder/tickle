package uk.co.nickthecoder.tickle.editor.tabs

import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.ButtonField
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.*

class InputTab(name: String, input: CompoundInput)
    : EditTaskTab(InputTask(name, input), "Input", name, input, graphicName = "input.png") {

    init {
        addDeleteButton { Resources.instance.deleteInput(name) }
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
        val i = Resources.instance.optionalInput(nameP.value)
        if (i != null && i != compoundInput) {
            throw ParameterException(nameP, "This name is already used.")
        }
    }

    override fun run() {
        if (nameP.value != name) {
            Resources.instance.renameInput(name, nameP.value)
        }
        compoundInput.inputs.clear()
        inputsP.innerParameters.forEach {
            it.toInput()?.let { compoundInput.add(it) }
        }
    }

}

class InputParameter : MultipleGroupParameter("input") {

    // KEY

    val chooseKeyP = ButtonParameter("keyPress", label = "", buttonText = "Click then type to pick a key") { onChooseKey(it) }

    val keyP = ChoiceParameter<Key?>("key", required = true, value = null).nullableEnumChoices(mixCase = true)

    val keyInfoP = InformationParameter("keyInfo", information = "The following field is only used when using KeyEvents, not when checking if a key is currently down.")

    val keyStateP = ChoiceParameter<ButtonState>("keyState", label = "State", value = ButtonState.PRESSED).enumChoices(mixCase = true)

    val keyInputP = SimpleGroupParameter("keyInput", label = "Keyboard")
            .addParameters(keyP, chooseKeyP, keyInfoP, keyStateP)

    // MOUSE

    val mouseButtonP = IntParameter("mouseButton", minValue = 0, value = 0)

    val mouseStateP = ChoiceParameter<ButtonState>("mouseState", label = "State", value = ButtonState.PRESSED).enumChoices(mixCase = true)

    val mouseInputP = SimpleGroupParameter("mouseInput", label = "Mouse")
            .addParameters(mouseButtonP, mouseStateP)

    // One Of ...

    val inputTypeP = OneOfParameter("inputType", label = " ", value = keyInputP, choiceLabel = "Input Type")
            .addParameters(keyInputP, mouseInputP).asPlain()

    private var keyPressHandler: EventHandler<KeyEvent>? = null

    init {
        addParameters(inputTypeP)
    }

    fun from(input: Input) {

        println("Input = ${input}")

        if (input is KeyInput) {
            inputTypeP.value = keyInputP
            keyP.value = input.key
            keyStateP.value = input.state

        } else if (input is MouseInput) {
            inputTypeP.value = mouseInputP
            mouseButtonP.value = input.mouseButton
            mouseStateP.value = input.state
        }
    }

    fun toInput(): Input? {
        if (inputTypeP.value == keyInputP) {
            return KeyInput(keyP.value!!, keyStateP.value!!)
        } else if (inputTypeP.value == mouseInputP) {
            return MouseInput(mouseButtonP.value!!, mouseStateP.value!!)
        }
        return null
    }


    private fun onChooseKey(buttonField: ButtonField) {

        keyPressHandler = EventHandler { event ->
            if (event.code != KeyCode.SHIFT && event.code != KeyCode.CONTROL && event.code != KeyCode.ALT) {
                keyP.value = guessKey(event.code)
                buttonField.button?.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressHandler)
            }
        }
        buttonField.button?.addEventFilter(KeyEvent.KEY_PRESSED, keyPressHandler)

    }

    override fun toString(): String {
        return if (inputTypeP.value == keyInputP) {
            "Key ${keyP.value ?: "<unknown>"}"
        } else if (inputTypeP.value == mouseInputP) {
            "Mouse button ${mouseButtonP.value ?: "<unknown>"}"
        } else {
            "<new>"
        }
    }

    fun guessKey(keyCode: KeyCode): Key? {
        return Key.forLabel(keyCode.getName())
    }
}

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
    : EditTaskTab(InputTask(name, input), "Input", name, input) {

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
        inputsP.asListDetail { it.toString() }

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

    val keyP = ChoiceParameter<Key?>("key", required = true, value = null).nullableEnumChoices(mixCase = true)
    val keyEventTypeP = ChoiceParameter<KeyEventType>("type", value = KeyEventType.PRESS).enumChoices(mixCase = true)
    val chooseKeyP = ButtonParameter("keyPress", label = "", buttonText = "Click then type to pick a key") { onChooseKey(it) }
    val keyInputP = SimpleGroupParameter("keyInput", label = "Keyboard")
            .addParameters(keyP, keyEventTypeP, chooseKeyP)

    val inputTypeP = OneOfParameter("inputType", label = " ", value = keyInputP, choiceLabel = "Input Type")
            .addParameters(keyInputP).asPlain()

    private var keyPressHandler: EventHandler<KeyEvent>? = null

    init {
        addParameters(inputTypeP)
    }

    fun from(input: Input) {
        println("Input = ${input}")
        if (input is KeyInput) {
            inputTypeP.value = keyInputP
            keyP.value = input.key
            keyEventTypeP.value = input.type
        }
        // TODO Add MouseInput when that is implemented.
    }

    fun toInput(): Input? {
        if (inputTypeP.value == keyInputP) {
            return KeyInput(keyP.value!!, keyEventTypeP.value!!)
        }
        // TODO Add MouseInput when that is implemented.
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
        } else {
            "<new>"
        }
    }

    fun guessKey(keyCode: KeyCode): Key? {
        return Key.forLabel(keyCode.getName())
    }
}



package uk.co.nickthecoder.tickle.events

import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable


class CompoundInput : Input, Deletable, Renamable {

    val inputs = mutableSetOf<Input>()

    fun add(input: Input) {
        inputs.add(input)
    }

    fun remove(input: Input) {
        inputs.remove(input)
    }

    override fun isPressed(): Boolean {
        return inputs.firstOrNull { it.isPressed() } != null
    }

    override fun matches(event: KeyEvent): Boolean {
        return inputs.firstOrNull { it.matches(event) } != null
    }


    override fun delete() {
        Resources.instance.inputs.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.inputs.rename(this, newName)
    }

}

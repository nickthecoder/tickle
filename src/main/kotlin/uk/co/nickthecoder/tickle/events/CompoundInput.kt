package uk.co.nickthecoder.tickle.events


class CompoundInput() : Input {

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

}

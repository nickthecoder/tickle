package uk.co.nickthecoder.tickle.physics

interface FilterBits {
    fun values(): Map<String, Int>

    fun columns(): Int = 4
}

class NoFilterBits : FilterBits {
    override fun values(): Map<String, Int> = emptyMap()
}

class ExampleFilterBits : FilterBits {
    override fun values(): Map<String, Int> {
        return ExampleFilterBit.values().associateBy({ it.name }, { it.bit })
    }
}

enum class ExampleFilterBit(val bit: Int) {
    A(1), B(2), C(4), D(8);
}

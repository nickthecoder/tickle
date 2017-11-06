package uk.co.nickthecoder.tickle.physics

interface FilterGroups {
    fun values(): Map<String, Int>
}

class NoFilterGroups : FilterGroups {
    override fun values(): Map<String, Int> = mapOf("None" to 0)
}

class ExampleFilterGroups : FilterGroups {
    override fun values(): Map<String, Int> {
        return ExampleFilterGroup.values().associateBy({ it.name }, { it.index })
    }
}

enum class ExampleFilterGroup(val index: Int) {
    None(0), Land(1), Sea(2), Air(3)
}

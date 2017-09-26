package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor


class GameStage(override val name: String) : Stage {

    override val actors = mutableSetOf<Actor>()

    override fun begin() {
        actors.map { it.role }.filterNotNull().forEach { role ->
            role.activated()
        }
    }

    override fun end() {
        actors.map { it.role }.filterNotNull().forEach { role ->
            role.end()
        }
        actors.clear()
    }

    override fun tick() {
        actors.map { it.role }.filterNotNull().forEach { role ->
            role.tick()
        }
    }

    override fun add(actor: Actor) {
        actors.add(actor)
        actor.role?.begin()
    }

    override fun remove(actor: Actor) {
        actors.remove(actor)
        actor.role?.end()
    }

}

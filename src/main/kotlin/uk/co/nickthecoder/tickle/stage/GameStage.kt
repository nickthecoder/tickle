package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor


class GameStage() : Stage {

    private val mutableViews = mutableListOf<StageView>()

    override val views: List<StageView> = mutableViews

    private val mutableActors = mutableSetOf<Actor>()

    override val actors: Set<Actor> = mutableActors

    override fun begin() {
        actors.map { it.role }.filterNotNull().forEach { role ->
            role.begin()
        }
    }

    override fun activated() {
        actors.map { it.role }.filterNotNull().forEach { role ->
            role.activated()
        }
    }

    override fun end() {
        actors.map { it.role }.filterNotNull().forEach { role ->
            role.end()
        }
        mutableActors.clear()
    }

    override fun tick() {
        actors.map { it.role }.filterNotNull().forEach { role ->
            role.tick()
        }
    }

    override fun add(actor: Actor, activate: Boolean) {
        mutableActors.add(actor)
        actor.stage = this
        actor.role?.begin()
        if (activate) {
            actor.role?.activated()
        }
    }

    override fun remove(actor: Actor) {
        mutableActors.remove(actor)
        actor.role?.end()
    }

    override fun addView(view: StageView) {
        mutableViews.add(view)
    }
}

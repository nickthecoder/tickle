package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.physics.TickleWorld


class GameStage() : Stage {

    private val mutableViews = mutableListOf<StageView>()

    override val views: List<StageView> = mutableViews

    override var world: TickleWorld? = null

    private val mutableActors = mutableSetOf<Actor>()

    override val actors: Set<Actor> = mutableActors


    override fun begin() {
        actors.forEach { actor ->
            val role = actor.role
            role?.begin()
        }
    }

    override fun activated() {
        actors.forEach { actor ->
            val role = actor.role
            role?.activated()
        }
    }

    override fun end() {
        actors.forEach { actor ->
            val role = actor.role
            role?.end()
        }
        mutableActors.clear()
    }

    protected fun tickRoles() {
        actors.forEach { actor ->
            val role = actor.role
            role?.tick()
        }
    }

    override fun tick() {
        tickRoles()
        world?.tick()
    }

    override fun add(actor: Actor, activate: Boolean) {

        actor.costume.bodyDef?.let { bodyDef ->
            world?.createBody(bodyDef, actor)
        }

        mutableActors.add(actor)
        actor.stage = this
        actor.role?.begin()
        if (activate) {
            actor.role?.activated()
        }
    }

    override fun remove(actor: Actor) {
        actor.body?.let {
            world?.destroyBody(it)
            actor.body = null
        }
        mutableActors.remove(actor)
        actor.role?.end()
    }

    override fun addView(view: StageView) {
        mutableViews.add(view)
    }

}

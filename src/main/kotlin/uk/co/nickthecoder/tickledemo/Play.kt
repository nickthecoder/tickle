package uk.co.nickthecoder.tickledemo

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.CenterView
import uk.co.nickthecoder.tickle.action.CenterViewBetween
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.neighbourhood.Occupant
import uk.co.nickthecoder.tickle.neighbourhood.StandardNeighbourhood
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.stage.ZOrderStageView
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.TagManager

class Play : AbstractDirector() {

    val tagManager = TagManager()

    val neighbourhood = StandardNeighbourhood<Occupant>(60.0)

    @Attribute
    var degrees = 0.0

    val clockwise = Resources.instance.inputs.find("clockwise")!! // Z : Rotate the view
    val antiClockwise = Resources.instance.inputs.find("anti-clockwise")!! // X : Rotate the view
    val reset = Resources.instance.inputs.find("reset")!! // O : Reset the rotation to 0°
    val toggle = Resources.instance.inputs.find("toggle")!! // TAB : Toggles between Controllable roles.

    var activeControllable: Controllable? = null

    var centerAction: Action? = null
        set(v) {
            v?.begin()
            field = v
        }


    lateinit var stage: Stage
    lateinit var stageView: ZOrderStageView

    init {
        instance = this
    }

    override fun begin() {
        Game.instance.mergeScene("info")
        stage = Game.instance.scene.findStage("main")!!
        stageView = Game.instance.scene.findStageView("main")!! as ZOrderStageView
    }

    override fun activated() {
        tagManager.findARole(DemoTags.CONTROLLABLE)?.let {
            val con = it as Controllable
            con.hasInput = true
            activeControllable = con
            centerAction = CenterView(stageView, con.actor.position)
        }

    }

    override fun postTick() {
        if (reset.isPressed()) {
            degrees = 0.0
        }
        if (clockwise.isPressed()) {
            degrees -= 2
        }
        if (antiClockwise.isPressed()) {
            degrees += 2
        }
        centerAction?.act()
        stageView.direction.degrees = degrees

    }

    override fun onKeyEvent(event: KeyEvent) {
        super.onKeyEvent(event)

        if (toggle.matches(event)) {

            tagManager.closest(activeControllable!!, DemoTags.CONTROLLABLE)?.let {

                if (it is Controllable) {
                    centerAction = CenterViewBetween(stageView, activeControllable!!.actor.position, it.actor.position)
                            .then(ChangeControllable(it))
                    activeControllable?.hasInput = false
                } else {
                    System.err.println("ERROR. ${it} found to be CONTROLLABLE, but is not of type Controllable")
                }
            }
        }
    }

    inner class ChangeControllable(
            val newControllable: Controllable
    ) : Action {

        override fun act(): Boolean {
            centerAction = CenterView(stageView, newControllable.actor.position)
            newControllable.hasInput = true
            activeControllable = newControllable
            return true
        }
    }

    companion object {
        lateinit var instance: Play
    }
}

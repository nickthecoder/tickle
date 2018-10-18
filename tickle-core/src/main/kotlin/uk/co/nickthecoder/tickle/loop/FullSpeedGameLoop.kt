/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.loop

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.physics.TickleWorld

/**
 * This is the default implementation of [GameLoop].
 *
 * Note. This implementation call tick on Producer, Director, all StageViews, and all Stage (and therefore all Roles)
 * every frame. There is no attempt to keep a constant frame rate.
 * Therefore under heavy load, the time between two ticks can vary, and there is no mechanism to even out these
 * steps to prevent objects moving at different speeds depending on the load.
 *
 * If your games uses physics (JBox2d), then TickleWorld ensures that the time step is consistent.
 * i.e. objects WILL move at a constant speed, regardless of load. (at the expense of potential stuttering).
 *
 * Runs as quickly as possible. It will be capped to the screen's refresh rate if using v-sync.
 * See [Window.enableVSync].
 */
class FullSpeedGameLoop(game: Game) : AbstractGameLoop(game) {

    override fun sceneStarted() {
        resetStats()
        game.scene.stages.values.forEach { stage ->
            stage.world?.resetAccumulator()
        }
    }

    override fun tick() {

        tickCount++

        with(game) {
            producer.preTick()
            director.preTick()

            producer.tick()
            director.tick()

            if (!game.paused) {

                game.scene.views.values.forEach { it.tick() }
                game.scene.stages.values.forEach { it.tick() }

                // Call tick on all TickleWorlds
                // Each stage can have its own TickleWorld. However, in the most common case, there is only
                // one tickle world shared by all stages. Given the way that TickleWorld keeps a constant
                // time step, we COULD just call tick on every stage's TickleWorld without affecting the game.
                // However, debugging information may be misleading, as the first stage will cause a step, and
                // the subsequent stages will skip (because the accumulator is near zero).
                // So, this code ticks all Stages' worlds, but doesn't bother doing it twice for the same world.
                var world: TickleWorld? = null
                game.scene.stages.values.forEach { stage ->
                    val stageWorld = stage.world
                    if (stageWorld != null) {
                        if (stageWorld != world) {
                            world = stageWorld
                            stageWorld.tick()
                        }
                    }
                }
            }

            director.postTick()
            producer.postTick()

            mouseMoveTick()
            processRunLater()

        }

        game.scene.draw(game.renderer)
        game.window.swap()
    }

}

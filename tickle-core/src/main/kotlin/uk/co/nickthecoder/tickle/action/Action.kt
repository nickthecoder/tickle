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
package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.action.animation.Ease

/**
 * Action is a powerful way to make coding games easier in two ways.
 * In Tickle (as with most game engines), each game object is called once per frame. In Tickle these are "tick" methods.
 * The tick method must return quickly (all tick methods combined last for 1/60th second at most).
 * Therefore, if we want and object to move left and right with a delay in between, we cannot writing something like :
 *
 *     for ( 10 ) {
 *         move left( 2 )
 *     }
 *     sleep( 5 )
 *     for ( 10 ) {
 *         move right( 2 )
 *     }
 *     sleep( 5 )
 *
 * (If we did, then one frame would take many seconds, and the object would NOT appear to move!)
 *
 * Without something like [Action], the tick() code would be cumbersome. Maybe something like this :
 *
 *     def state = 0
 *     def sleepCountdown = 0
 *     def moveCount = 0
 *
 *     tick() {
 *         if (state == 0) {
 *             moveCount ++
 *             if (moveCount == 10) {
 *                 state = 1
 *                 sleepCountdown = 300 // (5seconds * 60)
 *             } else {
 *                 move left( 2 )
 *             }
 *         } else if (state == 1) {
 *             sleepCountdown --
 *             if (sleepCountdown <= 0) {
 *                 state = 2
 *             }
 *         } else if ...
 *     }
 *
 * This is horrible to write, and even worse to read and debug!
 *
 * [Action]s allow us to compose a task into pieces, where each piece is simple, but knows how to work in the constraints
 * of tick().
 * Here's the equivalent pseudo-code using Actions :
 *
 *     def action = Move( -2, 0 ).repeat(10).then( Delay( 5 ) ).then( Move ( 2, 0 ).repeat(10) ).then( Delay( 5 ) )
 *
 * This creates a structure which describes the actions that need to take place, it does NOT perform those actions.
 * Note that "Move", and "Delay" are classes of type [Action]. "repeat" and "then" are methods
 * ([Action.repeat], [Action.then]. Also, [Action.and] and [Action.forever]).
 *
 * To perform the actions :
 *
 *     tick() {
 *         if (action.act()) {
 *             // Do something, now that the action has finished. Maybe action.die() ???
 *         }
 *     }
 *
 * "action.act" will perform one tick's worth of work, and return true iff the whole action has finished
 *
 * However, with Actions, we can do better than that, because instead of moving 2 pixels 10 times, we could say
 * how far we need to travel in a given time like so :
 *
 *     MoveBy( -10, 0, 0.5 ) // Move left 10 in half a second
 *
 * and then it gets much better when we introduce the concept of an "Ease" function. See [Ease] for more details.
 * For example, if we use "EaseInOut", then the movement would start slow, get faster, and then slow down again.
 * This gives a much smoother motion.
 *
 * Tickle contains numerous built in Actions, and it is quite easy to create your own too.
 *
 * Note. You should always start an action before using it, so the example above missed out :
 *
 *     action.begin()
 *
 * In many cases using [ActionHolder] is easier than using [Action] alone, as it takes care of calling [begin],
 * allows you to check if the action has finished, and also allows the action to be dynamically extended
 * (using [ActionHolder.then] and [ActionHolder.and]), while the action is still active (without restarting it).
 *
 * As Role is the most common piece of game code, there is a special class for Roles to perform actions called
 * ActionRole (which is a sub-class of [ActionHolder]).
 *
 */
interface Action {

    /**
     *  Returns true iff the action is complete, and therefore act should not be called.
     */
    fun begin(): Boolean = false

    /**
     * Returns true iff the action is complete (and should not be called again).
     */
    fun act(): Boolean

    fun beginAndAct(): Boolean {
        if (begin()) {
            return true
        } else {
            return act()
        }
    }

    /**
     * Creates a [SequentialAction], so that [other] will act after this.
     * Note, if this action has already been started, it will be restarted when the [SequentialAction] is started.
     * If this isn't the behaviour you want, then consider using [ActionHolder].
     */
    fun then(other: Action): SequentialAction {
        return SequentialAction(this, other)
    }

    /**
     * Adds an arbitrary piece of code sequentially after this action.
     */
    fun then(func: () -> Unit): SequentialAction {
        return then(Do(func))
    }

    /**
     * Creates a [ParallelAction], so that this will act "simultaneously with [other].
     *
     * Note, if this action has already been started, it will be restarted when the [ParallelAction] is started.
     * If this isn't the behaviour you want, then consider using [ActionHolder].
     */
    fun and(other: Action): ParallelAction {
        return ParallelAction(this, other)
    }

    /**
     * Adds an arbitrary piece of code in parallel with this action.
     */
    fun and(func: () -> Unit): ParallelAction {
        return and(Do(func))
    }

    /**
     * Creates a [ForeverAction], i.e. one which repeats this action forever.
     *
     * Note, if this action has already been started, it will be restarted when the [ForeverAction] is started.
     */
    fun forever(): ForeverAction {
        return ForeverAction(this)
    }

    /**
     * Creates a [RepeatAction] action, i.e. one that repeats this action n times.
     *
     * Note, if this action has already been started, it will be restarted when the [RepeatAction] is started.
     */
    fun repeat(times: Int): RepeatAction {
        return RepeatAction(this, times)
    }

    fun repeatWhilst(conditional: Action) = RepeatWhilstAction(conditional, this)

    /**
     * Creates a [WhilstAction]. i.e. the combined Action will continue to act on the [conditional] Action,
     * and when it finishes, this action will also end.
     * If this Action ends first, then the [conditional] will continue as normal.
     *
     * For example, if we create an action which performs an animation, and only want it performed repeatedly
     * for 10 seconds, we could do :
     *
     *     animation.forever().whilst( Delay( 10 ) )
     *
     * Note, this will almost certainly stop the animation part way through. So instead, consider :
     *
     *     animation.repeatWhilst( Delay( 10 ) )
     *
     * Note, if this Action has already been started, then this Action will be restart when the WhilstAction is started.
     */
    fun whilst(conditional: Action) = WhilstAction(conditional, this)

    /**
     * Creates a [WhilstBoolean].
     * This is named "whilst" rather than "while", only to avoid a clash with the Java/Kotlin keyword!
     *
     * Does the same as [whilst], but allows the 'body' action to end prematurely early.
     *
     * If we make an analogy with a regular Java/Kotlin while statement, then ending early is like adding :
     *
     *     if (!condition) break
     *
     * after ever statement in the body of the loop! Weird, but useful ;-)
     */
    fun whilstEndEarly(conditional: () -> Boolean, endBodyEarly: Boolean) = WhilstBoolean(this, conditional, true)

    /**
     * Creates a [WhilstBoolean].
     * This is named "whilst" rather than "while", only to avoid a clash with the Java/Kotlin keyword!
     *
     * Note, if this Action has already been started, then it will be restart when the UntilAction is started.
     */
    fun whilst(conditional: () -> Boolean) = WhilstBoolean(this, conditional, false)

    /**
     * Creates an [UntilBoolean]. i.e. this Action will be repeatedly restarted if it finishes before the [conditional].
     * When the [conditional] ends, the UntilBoolean action will end once this Action ends for the last time.
     *
     * Note, if this Action has already been started, then it will be restart when the UntilAction is started.
     */
    fun until(conditional: () -> Boolean) = UntilBoolean(this, conditional)


}

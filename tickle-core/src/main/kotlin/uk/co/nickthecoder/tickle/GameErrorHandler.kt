package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.util.ErrorHandler

interface GameErrorHandler {

    fun roleError(role: Role, e: Exception)

}

class SimpleGameErrorHandler : GameErrorHandler {

    override fun roleError(role: Role, e: Exception) {
        try {
            System.err.println("Exception thrown by role : ${role}. Removing from Stage, to prevent more errors.")
            ErrorHandler.handleError(e)
            role.actor.stage?.remove(role.actor)
        } catch (e2: Exception) {
            // Just in case the "remove" fails
            ErrorHandler.handleError(e2)
        }
    }

}

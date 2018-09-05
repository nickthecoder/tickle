package uk.co.nickthecoder.tickle

interface ErrorHandler {

    fun roleError(role: Role, e: Exception)

}

class SimpleErrorHandler : ErrorHandler {

    override fun roleError(role: Role, e: Exception) {
        try {
            System.err.println("Exception thrown by role : ${role}. Removing from Stage, to prevent more errors.")
            e.printStackTrace()
            role.actor.stage?.remove(role.actor)
        } catch (e2: Exception) {
            System.err.println("Exception thrown inside SimpleErrorHandler!")
            e2.printStackTrace()
        }
    }


}
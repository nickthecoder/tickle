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
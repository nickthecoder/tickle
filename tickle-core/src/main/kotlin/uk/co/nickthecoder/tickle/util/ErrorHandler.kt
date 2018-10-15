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
package uk.co.nickthecoder.tickle.util

interface ErrorHandler {
    fun handleError(e: Exception)

    companion object {
        var errorHandler: ErrorHandler = DumpErrorHandler()
            set(v) {
                val old = field
                field = v
                if (old is DelayedErrorHandler) {
                    old.forwardErrors(v)
                }
            }

        fun handleError(e: Exception) {
            errorHandler.handleError(e)
        }
    }
}

class DelayedErrorHandler : ErrorHandler {

    private val exceptions = mutableListOf<Exception>()

    override fun handleError(e: Exception) {
        exceptions.add(e)
    }

    fun forwardErrors(handler: ErrorHandler) {
        exceptions.forEach { handler.handleError(it) }
        exceptions.clear()
    }
}

class DumpErrorHandler : ErrorHandler {
    override fun handleError(e: Exception) {
        e.printStackTrace()
    }
}
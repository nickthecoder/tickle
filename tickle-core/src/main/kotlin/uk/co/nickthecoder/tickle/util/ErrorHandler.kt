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
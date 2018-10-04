package uk.co.nickthecoder.tickle.scripts

import java.io.File

class ScriptException(message: String, cause: Exception, val file: File?, val line: Int?, val column: Int?) : Exception(message, cause) {

    constructor(cause: Exception) : this(cause.message ?: "", cause, null, null, null)

}

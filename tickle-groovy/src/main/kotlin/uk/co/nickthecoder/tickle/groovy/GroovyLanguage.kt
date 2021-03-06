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
package uk.co.nickthecoder.tickle.groovy

import groovy.lang.GroovyRuntimeException
import groovy.util.GroovyScriptEngine
import uk.co.nickthecoder.tickle.Director
import uk.co.nickthecoder.tickle.Producer
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.scripts.Language
import uk.co.nickthecoder.tickle.scripts.ScriptException
import java.io.File
import java.util.regex.Pattern


class GroovyLanguage : Language() {

    private lateinit var engine: GroovyScriptEngine

    override val fileExtension = "groovy"

    override val name = "Groovy"

    private lateinit var path: File

    override fun setClasspath(directory: File) {
        path = directory
        createEngine()
    }

    private fun createEngine() {
        engine = GroovyScriptEngine(path.path)
        engine.config.minimumRecompilationInterval = 0
    }

    override fun loadScript(file: File): Class<*> {
        try {
            return engine.loadScriptByName(file.name)
        } catch (e: GroovyRuntimeException) {
            throw convertException(e)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ScriptException(e)
        }
    }

    override fun clear() {
        super.clear()
        // By creating a new engine, I hope that the classes used by the previous engine's
        // classloader can be garbage collected.
        createEngine()
    }

    override fun generateScript(name: String, type: Class<*>?): String {
        return when (type) {
            Role::class.java -> generateRole(name)
            Director::class.java -> generateDirector(name)
            Producer::class.java -> generateProducer(name)
            else -> generatePlainScript(name, type)
        }
    }

    fun generatePlainScript(name: String, type: Class<*>?) = """import uk.co.nickthecoder.tickle.*
${if (type == null || type.`package`.name == "uk.co.nickthecoder.tickle") "" else "import ${type.name}"}
import uk.co.nickthecoder.tickle.resources.*

class $name ${if (type == null) "" else (if (type.isInterface) "implements" else "extends") + " ${type.simpleName}"}{

}

"""

    fun generateRole(name: String) = """import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.resources.*

class $name extends AbstractRole {

    // NOTE. Some common methods were automatically generated.
    // These may be removed if you don't need them.

    void begin() {
    }

    void activated() {
    }

    // tick is called 60 times per second (a Role MUST have a tick method).

    void tick() {
    }

}

"""

    fun generateDirector(name: String) = """import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.events.*
class $name extends AbstractDirector {

    // NOTE. Some common methods were automatically generated.
    // These may be removed if you don't need them.

    void sceneLoaded() {
    }

    void begin() {
    }

    void activated() {
    }

    void onKey(KeyEvent event) {
    }

    void onMouseButton(MouseEvent event) {
    }
}

"""

    fun generateProducer(name: String) = """import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.events.*

class $name extends AbstractProducer {

    // NOTE. Some common methods were automatically generated.
    // These may be removed if you don't need them.

    void begin() {
    }

    void sceneLoaded() {
    }

    void sceneBegin() {
    }

    void sceneEnd() {
    }

    void sceneActivated() {
    }

    void tick() {
    }

    void onKey(KeyEvent event) {
    }

    void onMouseButton(MouseEvent event) {
    }
}

"""

    companion object {

        private val messagePattern = Pattern.compile("file\\:(?<FILE>.*?\\.groovy)|line (?<LINE>\\d*), column (?<COLUMN>\\d*)")

        fun convertException(e: GroovyRuntimeException): ScriptException {
            var message = e.message ?: return ScriptException(e)

            val matcher = messagePattern.matcher(message)

            var file: File? = null
            var line: Int? = null
            var column: Int? = null

            while (matcher.find()) {
                matcher.group("FILE")?.let { file = File(it) }
                matcher.group("LINE")?.let { line = it.toInt() }
                matcher.group("COLUMN")?.let {
                    column = it.toInt()
                    message = message.substring(0, matcher.end())
                }
            }
            message = message.removePrefix("startup failed:\n")
            return ScriptException(message, e, file, line, column)
        }

    }

}

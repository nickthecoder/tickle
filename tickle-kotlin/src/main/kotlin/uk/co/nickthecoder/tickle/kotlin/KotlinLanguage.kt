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
package uk.co.nickthecoder.tickle.kotlin

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import uk.co.nickthecoder.tickle.scripts.Language
import java.io.File

/**
 * Allows Kotlin to be used as a scripting language.
 *
 * I don't think this works very well though, so for now, I will stick with Groovy as the default scripting language.
 *
 * The error messages returned by the kotlin script interpreter are dreadful.
 * The class names are weird (such as Line_3$ExampleRole). I have bodged this though, by using the filename instead
 * of the class name.
 */
class KotlinLanguage : Language() {

    val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine

    override val fileExtension = "kts"

    override val name = "Kotlin"

    override fun setClasspath(directory: File) {
        // TODO  Hmm, we don't seem to have a way to add the scripts directory to the script engine's path.
        // Which means there is no way to create an abstract Role in one file, and extend it in another.
        // Nor is it possible to use another user-defined class from within multiple Role subtypes.
        // Grr. It looks like Kotlin isn't suitable as a scripting language (yet?).
    }

    override fun loadScript(file: File): Class<*> {
        engine.eval(file.reader())
        return engine.eval("${file.nameWithoutExtension}::class.java") as Class<*>
    }

    override fun generateScript(name: String, type: Class<*>?) = """import uk.co.nickthecoder.tickle.*

class $name {

}
"""
}

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


class KotlinLanguage : Language() {

    val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine

    override val fileExtension = "kts"

    override fun addPath(directory: File) {
        // TODO  Hmm, we don't seem to have a way to add the scripts directory to the script engine's path.
        // Which means there is no way to create an abstract Role in one file, and extend it in another.
    }

    override fun loadScript(file: File): Class<*> {
        engine.eval(file.reader())
        return engine.eval("${file.nameWithoutExtension}::class.java") as Class<*>
    }

}

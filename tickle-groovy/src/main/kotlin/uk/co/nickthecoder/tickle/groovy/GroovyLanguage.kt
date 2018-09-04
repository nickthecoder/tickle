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

import groovy.lang.GroovyClassLoader
import uk.co.nickthecoder.tickle.Director
import uk.co.nickthecoder.tickle.Producer
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.scripts.Language
import java.io.File


class GroovyLanguage : Language() {

    val groovyClassLoader = GroovyClassLoader(javaClass.classLoader)

    override val fileExtension = "groovy"

    override val name = "Groovy"

    override fun addPath(directory: File) {
        groovyClassLoader.addClasspath(directory.path)
    }

    override fun loadScript(file: File): Class<*> {
        return groovyClassLoader.parseClass(file)
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

class $name ${if (type == null) "" else (if (type.isInterface) "implements" else "extends") + " ${type.simpleName}"}{

}

"""

    fun generateRole(name: String) = """import uk.co.nickthecoder.tickle.*

class $name extends AbstractRole {

    // NOTE. Some common methods were automatically generated.
    // These may be removed if you don't need them.

    def void begin() {
    }

    def void activated() {
    }

    def void tick() {
    }

}

"""

    fun generateDirector(name: String) = """import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.*

class $name extends AbstractDirector {

    // NOTE. Some common methods were automatically generated.
    // These may be removed if you don't need them.

    def void sceneLoaded() {
    }

    def void begin() {
    }

    def void activated() {
    }

    def void() {
    }

    def void onKey(KeyEvent event) {
    }

    def void onMouseButton(MouseEvent event) {
    }
}

"""

    fun generateProducer(name: String) = """import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.*

class $name extends AbstractProducer {

    // NOTE. Some common methods were automatically generated.
    // These may be removed if you don't need them.

    def void begin() {
    }

    def void sceneLoaded() {
    }

    def void sceneBegin() {
    }

    def void sceneEnd() {
    }

    def void sceneActivated() {
    }

    def void tick() {
    }

    def void onKey(KeyEvent event) {
    }

    def void onMouseButton(MouseEvent event) {
    }
}

"""


}

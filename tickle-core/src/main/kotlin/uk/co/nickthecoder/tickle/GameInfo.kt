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

import uk.co.nickthecoder.tickle.physics.PhysicsInfo
import java.io.File

class GameInfo(
        var title: String,
        var id: String, /* simple text - no spaces or punctuation, will NOT be translated if I18N is implemented */
        var width: Int,
        var height: Int,
        var fullScreen : Boolean,
        var resizable: Boolean,
        var initialScenePath: File = File("menu"),
        var testScenePath: File = File("menu"),
        var producerString: String = NoProducer::class.java.name,
        var physicsEngine: Boolean = false) {

    val physicsInfo = PhysicsInfo()

    fun createProducer(): Producer {
        try {
            val klass = Class.forName(producerString)
            val newProducer = klass.newInstance()
            if (newProducer is Producer) {
                return newProducer
            } else {
                System.err.println("'$producerString' is not a type of Producer")
            }
        } catch (e: Exception) {
            System.err.println("Failed to create a Producer from : '$producerString'")
        }
        return NoProducer()
    }

}


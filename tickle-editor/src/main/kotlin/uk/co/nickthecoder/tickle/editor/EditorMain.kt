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
package uk.co.nickthecoder.tickle.editor

import javafx.application.Application
import uk.co.nickthecoder.tickle.Tickle

/**
 * Extends the [Tickle] entry point class, adding options --editor and --new.
 *
 * Note. The [Tickle] entry point is in the 'core' module, which does NOT depend on paratask,
 * or JavaFX, and therefore cannot launch the editor, or the [NewGameWizardApp].
 */
class EditorMain(programName: String, args: Array<String>) : Tickle(programName, args) {

    var startEditor = false
    var newWizard = false

    override fun parseArg(i: Int): Int {
        when (args[i]) {
            "--editor" -> {
                startEditor = true
                return 1
            }
            "--new" -> {
                newWizard = true
                return 1
            }
        }

        return super.parseArg(i)
    }

    override fun launch() {
        if (startEditor) {

            println("Starting editor using resources file : $resourcesFile")
            resourcesFile?.let { Editor.start(it) }

        } else if (newWizard) {
            Application.launch(NewGameWizardApp::class.java)

        } else {
            super.launch()
        }
    }

    override fun helpStart() {
        super.helpStart()
        println("Or    : $programName --editor [RESOURCE_FILE] (Starts the editor)")
        println("Or    : $programName --new (Starts a wizard to aid creating a new game)")

    }
}

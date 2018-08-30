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

import com.eclipsesource.json.WriterConfig

class EditorPreferences {

    var outputFormat: JsonFormat = JsonFormat.PRETTY

    val packages: MutableList<String> = mutableListOf("uk.co.nickthecoder.tickle")

    var treeThumnailSize: Int = 24

    var costumePickerThumbnailSize: Int = 40

    /**
     * The size of the scene used by the editor's MainWindow.
     * This allows the window to be the same size as the last time the editor was run.
     * Note, the size and isMaximized are NOT shown in the EditorPreferencesTask, and therefore
     * cannot be edited directly by the user.
     */
    var windowWidth = 1000.0

    var windowHeight = 600.0

    /**
     * Is the editor's MainWindow maximized? When true, the windowWidth and windowHeight are NOT
     * updated when the window closes
     */
    var isMaximized = false

    override fun toString(): String {
        return "outputFormat=$outputFormat packages=$packages"
    }

    enum class JsonFormat(val writerConfig: WriterConfig) {
        COMPACT(WriterConfig.MINIMAL),
        //UGLY(UglyPrint("  ")),
        PRETTY(WriterConfig.PRETTY_PRINT)
    }
}

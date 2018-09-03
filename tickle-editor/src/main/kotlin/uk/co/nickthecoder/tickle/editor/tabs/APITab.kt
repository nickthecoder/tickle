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
package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.control.TitledPane
import javafx.scene.web.WebView
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.tickle.editor.APIStub
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.resources.Resources


class APITab : EditTab("API Documentation", APIStub, graphicName = ResourceType.API_Documentation.graphicName),
        HasExtras {

    private val webView = WebView()
    val webEngine = webView.getEngine()

    private val classHelpPane = TitledPane("Classes", ClassHelpBox(webView))
    override fun extraSidePanes() = listOf(classHelpPane)

    private val shortcuts = ShortcutHelper("API Documentation", MainWindow.instance.borderPane)
    override fun extraShortcuts() = shortcuts

    init {
        webEngine.load(Resources.instance.preferences.apiURL)
        borderPane.center = webView
        okButton.isVisible = false
        applyButton.isVisible = false
        cancelButton.text = "Close"
    }

    override fun save() = true
}

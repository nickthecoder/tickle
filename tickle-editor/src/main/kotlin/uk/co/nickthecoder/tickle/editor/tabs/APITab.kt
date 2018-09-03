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

/**
 * Shows the API documentation using HTML pages from the web.
 * The main content of the tab contains a [WebView] (which you can think of as a web browser.
 * In addition, a side panel (to the left of the main content) shows a list of packages and classes.
 * This is implemented in the [APIBox] and [APITree] classes.
 * Clicking on a class or a package from the list updates the [WebView] to show the appropriate content.
 */
class APITab : EditTab("API Documentation", APIStub, graphicName = ResourceType.API_Documentation.graphicName),
        HasExtras {

    private val webView = WebView()
    private val webEngine = webView.getEngine()

    private val classHelpPane = TitledPane("Classes", APIBox(webView))
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

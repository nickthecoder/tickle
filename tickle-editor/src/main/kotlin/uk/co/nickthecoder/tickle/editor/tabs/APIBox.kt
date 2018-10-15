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

import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView

/**
 * Currently, this only hols an APITree (which lists packages and classes).
 * Later, this will be improved, to allow the tree to be filtered.
 * Either by limiting the classes to the most important ones (thus hiding those classes which are
 * very rarely used), or by searching for a class by name (or a partial name).
 */
class APIBox(webView: WebView) : BorderPane() {

    private val tree = APITree(webView)

    init {
        center = tree
    }
}

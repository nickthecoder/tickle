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

import uk.co.nickthecoder.tickle.editor.util.ResourceType
import uk.co.nickthecoder.tickle.resources.ResourceMap
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable

class CostumeGroup(resources: Resources)
    : ResourceMap<Costume>(resources, ResourceType.COSTUME), Deletable, Renamable {

    var showInSceneEditor: Boolean = true

    /**
     * A CostumeGroup can be deleted, even if it has Costumes (because those costumes become group-less).
     * So a CostumeGroup always returns null, and can therefore always be deleted.
     */
    override fun usedBy(): Any? = null

    override fun delete() {
        items().toMutableMap().forEach { name, costume ->
            remove(name)
            costume.costumeGroup = null
        }
        Resources.instance.costumeGroups.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.costumeGroups.rename(this, newName)
    }
}

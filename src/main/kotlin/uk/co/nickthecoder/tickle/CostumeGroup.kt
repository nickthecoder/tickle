package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.resources.ResourceMap
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable

class CostumeGroup(resources: Resources)
    : ResourceMap<Costume>(resources, "Costume"), Deletable, Renamable {

    var showInSceneEditor: Boolean = true

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

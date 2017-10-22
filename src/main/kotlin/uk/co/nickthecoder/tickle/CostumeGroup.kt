package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.resources.ResourceMap
import uk.co.nickthecoder.tickle.resources.Resources

class CostumeGroup(resources: Resources)
    : ResourceMap<Costume>(resources, "Costume") {

    var showInSceneEditor: Boolean = true

}

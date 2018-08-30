package uk.co.nickthecoder.tickle.editor.resources

import uk.co.nickthecoder.tickle.editor.util.DesignAttributes
import uk.co.nickthecoder.tickle.resources.Resources

class DesignResources : Resources() {

    override fun createAttributes(): DesignAttributes {
        return DesignAttributes()
    }

    fun save() {
        DesignJsonResources(this).save(this.file)
    }

}

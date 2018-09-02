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
package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource

class Selection : Iterable<DesignActorResource> {

    private val items = mutableSetOf<DesignActorResource>()

    private var latest: DesignActorResource? = null

    val listeners = mutableListOf<SelectionListener>()
    val size
        get() = items.size


    fun isEmpty() = items.isEmpty()

    fun isNotEmpty() = items.isNotEmpty()

    fun clear() {
        items.clear()
        latest = null
        fireChange()
    }

    fun add(obj: DesignActorResource?) {
        obj ?: return
        items.add(obj)
        latest = obj
        fireChange()
    }

    fun remove(obj: DesignActorResource?) {
        obj ?: return
        items.remove(obj)
        if (obj === latest) {
            latest = null
        }
        fireChange()
    }

    fun selected(): Set<DesignActorResource> = items

    fun latest(): DesignActorResource? = latest

    fun clearAndSelect(obj: DesignActorResource?) {
        clear()
        obj?.let { add(it) }
        fireChange()
    }

    fun fireChange() {
        listeners.forEach { it.selectionChanged() }
    }

    override fun iterator(): Iterator<DesignActorResource> = items.iterator()

}

interface SelectionListener {

    fun selectionChanged()

}

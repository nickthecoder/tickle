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
package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.Game
import java.io.OutputStream
import java.util.prefs.NodeChangeListener
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

/**
 * A wrapper around Java's [Preferences] class. Easier to use, as it auto-flushes, and also uses standard Kotlin idioms.
 */
class AutoFlushPreferences(private var wrapped: Preferences) : Preferences() {

    // Flush & Sync

    var dirty = false
        set(v) {
            if (v != field) {
                field = v
                if (v) {
                    Game.runLater {
                        flush()
                    }
                }
            }
        }

    fun autoFlush() {
        dirty = true
    }

    override fun flush() {
        wrapped.flush()
    }

    override fun sync() {
        wrapped.sync()
    }


    // Parent / Child methods

    override fun keys() = wrapped.keys()

    fun listKeys() = wrapped.keys().toList()

    override fun childrenNames(): Array<String> {
        return wrapped.childrenNames()
    }

    override fun name(): String = wrapped.name()


    override fun node(pathName: String): AutoFlushPreferences {
        return AutoFlushPreferences(wrapped.node(pathName))
    }

    override fun nodeExists(pathName: String): Boolean {
        return wrapped.nodeExists(pathName)
    }

    override fun parent(): AutoFlushPreferences {
        return AutoFlushPreferences(wrapped.parent())
    }


    // Get

    override operator fun get(key: String, def: String): String = wrapped.get(key, def)

    override fun getBoolean(key: String, def: Boolean): Boolean = wrapped.getBoolean(key, def)

    override fun getByteArray(key: String, def: ByteArray): ByteArray = wrapped.getByteArray(key, def)

    override fun getDouble(key: String, def: Double): Double = wrapped.getDouble(key, def)

    override fun getFloat(key: String, def: Float): Float = wrapped.getFloat(key, def)

    override fun getInt(key: String, def: Int): Int = wrapped.getInt(key, def)

    override fun getLong(key: String, def: Long): Long = wrapped.getLong(key, def)


    // Put / Set

    operator fun set(key: String, value: String) {
        put(key, value)
    }

    override fun put(key: String, value: String) {
        wrapped.put(key, value)
        autoFlush()
    }

    operator fun set(key: String, value: Boolean) {
        putBoolean(key, value)
    }

    override fun putBoolean(key: String, value: Boolean) {
        wrapped.putBoolean(key, value)
        autoFlush()
    }

    operator fun set(key: String, value: ByteArray) {
        putByteArray(key, value)
    }

    override fun putByteArray(key: String, value: ByteArray) {
        wrapped.putByteArray(key, value)
        autoFlush()
    }

    operator fun set(key: String, value: Double) {
        putDouble(key, value)
    }

    override fun putDouble(key: String, value: Double) {
        wrapped.putDouble(key, value)
        autoFlush()
    }

    operator fun set(key: String, value: Float) {
        putFloat(key, value)
    }

    override fun putFloat(key: String, value: Float) {
        wrapped.putFloat(key, value)
        autoFlush()
    }

    operator fun set(key: String, value: Int) {
        putInt(key, value)
    }

    override fun putInt(key: String, value: Int) {
        wrapped.putInt(key, value)
        autoFlush()
    }

    operator fun set(key: String, value: Long) {
        putLong(key, value)
    }

    override fun putLong(key: String, value: Long) {
        wrapped.putLong(key, value)
        autoFlush()
    }

    // Clear / Remove

    override fun clear() {
        wrapped.clear()
    }

    override fun remove(key: String) {
        wrapped.remove(key)
        autoFlush()
    }

    override fun removeNode() {
        val parent = wrapped.parent()
        val name = wrapped.name()

        wrapped.removeNode()
        autoFlush()

        // When deleting a node, we want to be able to recreate the node without having to create a new
        // AutoFlushPreferences object. Without this, we get an error if we try to read from this node
        // after removing it.
        wrapped = parent.node(name)
    }

    // Listeners

    override fun addNodeChangeListener(ncl: NodeChangeListener) {
        wrapped.addNodeChangeListener(ncl)
    }

    override fun addPreferenceChangeListener(pcl: PreferenceChangeListener) {
        wrapped.addPreferenceChangeListener(pcl)
    }

    override fun removeNodeChangeListener(ncl: NodeChangeListener) {
        wrapped.removeNodeChangeListener(ncl)
    }

    override fun removePreferenceChangeListener(pcl: PreferenceChangeListener) {
        wrapped.removePreferenceChangeListener(pcl)
    }


    // Miscellaneous

    override fun exportNode(os: OutputStream) {
        wrapped.exportNode(os)
    }

    override fun exportSubtree(os: OutputStream) {
        wrapped.exportSubtree(os)
    }

    override fun isUserNode(): Boolean = wrapped.isUserNode()

    override fun absolutePath(): String {
        return wrapped.absolutePath()
    }

    override fun toString() = wrapped.toString()

}

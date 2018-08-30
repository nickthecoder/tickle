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
package uk.co.nickthecoder.tickle.physics

import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.dynamics.contacts.Contact

/**
 * JBox2D (the underlying physics library) only has a single contact listener.
 * In some situations, it may be preferable to have more than one. For example, a Director may handle some contacts,
 * and individual Roles handle others.
 * CompoundContactListener forwards all contact notifications to a set of ContactListeners.
 * In the example above, we could create a CompoundContactListener with two inner listeners (the Director and a
 * RoleContactManager instance).
 *
 * [TickleWorld.addContactListener] uses a CompoundContactListener when more than one listener is added.
 */
class CompoundContactListener : ContactListener {

    val listeners = mutableListOf<ContactListener>()

    override fun beginContact(contact: Contact) {
        listeners.forEach { it.beginContact(contact) }
    }

    override fun endContact(contact: Contact) {
        listeners.forEach { it.endContact(contact) }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        listeners.forEach { it.preSolve(contact, oldManifold) }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        listeners.forEach { it.postSolve(contact, impulse) }
    }

}

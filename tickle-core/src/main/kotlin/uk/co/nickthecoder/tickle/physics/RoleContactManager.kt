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
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.contacts.Contact
import uk.co.nickthecoder.tickle.Actor

/**
 * A [ContactListener], which notifies Roles of type [ContactListenerRole] when they begin and end contact with another
 * [Actor]. Note that the other Actor does NOT need to have a role of type [ContactListenerRole], but if it does, then
 * that role will ALSO be notified of the contact (and the order is not predictable).
 *
 *
 */
class RoleContactManager : ContactListener {

    override fun beginContact(contact: Contact) {
        beginContact(contact, contact.fixtureA.body, contact.fixtureB.body)
        beginContact(contact, contact.fixtureB.body, contact.fixtureA.body)
    }

    fun beginContact(contact: Contact, bodyA: Body, bodyB: Body) {
        val role = bodyA.actor().role
        if (role is ContactListenerRole) {
            role.beginContact(contact, bodyB.actor())
        }
    }

    override fun endContact(contact: Contact) {
        endContact(contact, contact.fixtureA.body, contact.fixtureB.body)
        endContact(contact, contact.fixtureB.body, contact.fixtureA.body)
    }

    fun endContact(contact: Contact, bodyA: Body, bodyB: Body) {
        val role = bodyA.actor().role
        if (role is ContactListenerRole) {
            role.endContact(contact, bodyB.actor())
        }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

}

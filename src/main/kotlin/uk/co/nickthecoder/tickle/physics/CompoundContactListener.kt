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

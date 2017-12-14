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
        val role = (bodyA.userData as Actor).role
        if (role is ContactListenerRole) {
            role.beginContact(contact, bodyB.userData as Actor)
        }
    }

    override fun endContact(contact: Contact) {
        endContact(contact, contact.fixtureA.body, contact.fixtureB.body)
        endContact(contact, contact.fixtureB.body, contact.fixtureA.body)
    }

    fun endContact(contact: Contact, bodyA: Body, bodyB: Body) {
        val role = (bodyA.userData as Actor).role
        if (role is ContactListenerRole) {
            role.endContact(contact, bodyB.userData as Actor)
        }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

}

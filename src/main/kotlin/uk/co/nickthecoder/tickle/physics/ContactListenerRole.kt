package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.contacts.Contact
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Role

/**
 * Notified of physical contacts between actors. In order to receive notifications use :
 * [TickleWorld.setContactListener] or [TickleWorld.addContactListener].
 *
 * Note, either contact.fixtureA.body == this.actor.body OR contact.fixtureB.body == this.actor.body.
 * Similarly contact.fixtureA.body == otherActor.body OR contact.fixtureB.body == otherActor.body
 */
interface ContactListenerRole : Role {

    fun beginContact(contact: Contact, otherActor: Actor)

    fun endContact(contact: Contact, otherActor: Actor)

}

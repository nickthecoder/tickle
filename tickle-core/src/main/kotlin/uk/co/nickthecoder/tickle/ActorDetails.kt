package uk.co.nickthecoder.tickle

/**
 * Common attributes shared by both Actor and ActorResource.
 *
 * Currently, this is only used by StageView, so that Actors and ActorResources can be sorted when rendering, and
 * sorted in the opposite order when checking for Actors/ActorResources at a point.
 *
 * Other common attributes may be moved into here if the need arises.
 */
interface ActorDetails {

    var x: Double
    var y: Double
    var zOrder: Double

}

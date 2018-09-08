package uk.co.nickthecoder.tickle.util

/**
 * Any classes which implement SimpleInstance can be used as an [Attribute].
 * Instead of choosing a value, you choose which sub-class to instantiate.
 * For example, [ButtonActions] implements SimpleInstance, and therefore the [Button] role can
 * have a [ButtonActions] as an attribute. The user can then pick and sub-class of [ButtonActions]
 * as the value.
 *
 * Note, for this to work, all sub-classes must have a zero-argument constructor.
 */
interface SimpleInstance

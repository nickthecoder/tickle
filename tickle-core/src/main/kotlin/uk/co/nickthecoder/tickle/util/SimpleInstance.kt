package uk.co.nickthecoder.tickle.util

/**
 * Any classes which implement SimpleInstance can be used as an [Attribute].
 * Instead of choosing a value, you choose which sub-class to instantiate.
 * For example, [ButtonEffects] implements SimpleInstance, and therefore the [Button] role can
 * have a [ButtonEffects] as an attribute. The user can then pick and sub-class of [ButtonEffects]
 * as the value.
 *
 * Note, for this to work, all sub-classes must have a zero-argument constructor.
 */
interface SimpleInstance

package uk.co.nickthecoder.tickle.util

/**
 * All effects return null.
 *
 * Note, Kotlin allows the interface to return nulls, however, when we use Groovy for game development,
 * we cannot rely on Kotlin's features. Therefore Groovy code can extend this class.
 */
abstract class AbstractButtonEffects : ButtonEffects

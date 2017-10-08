package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.AttributeType

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Attribute(
        val attributeType: AttributeType = AttributeType.NORMAL,
        val order : Int = 1,
        val scale : Double = 1.0
)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class CostumeAttribute

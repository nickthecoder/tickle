package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.Role

interface TaggedRole : Role {
    val tagged: Tagged
}

package uk.co.nickthecoder.tickle.graphics

import uk.co.nickthecoder.tickle.FontResource

class TextStyle(
        var fontResource: FontResource,
        var halignment: HAlignment,
        var valignment: VAlignment,
        var color: Color) {

}

enum class HAlignment { LEFT, CENTER, RIGHT }
enum class VAlignment { BOTTOM, BASELINE, CENTER, TOP }

package uk.co.nickthecoder.tickle.sandbox

import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Rectd
import java.io.File

/**
 * Not finished!
 * Moves two items over each other, and at the same time, shows the intermediate results of pixel based overlap detection.
 */
class OverlapSandbox() : Sandbox() {

    val coin = Texture.create(File("src/dist/resources/images/coin.png"))
    val soil = Texture.create(File("src/dist/resources/images/soil.png"))

    var coinX = -100.0
    var coinY = 0.0

    var soilX = 100.0
    var soilY = 0.0

    val coinRect = Rectd(0.0, 0.0, 1.0, 1.0)
    val worldRect = Rectd()

    override fun tick() {
        renderer.beginView()
        renderer.clear()

        coinX ++

        worldRect.left = coinX
        worldRect.bottom = coinY
        worldRect.right = coinX + coin.width
        worldRect.top = coinY + coin.height
        renderer.drawTexture(coin, worldRect, coinRect)

        renderer.endView()
        window.swap()
    }
}

fun main(args: Array<String>) {
    OverlapSandbox().loop()
}

import javafx.application.*
import javafx.scene.image.*
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.util.*
import uk.co.nickthecoder.tickle.editor.tabs.FXCoderTab
import uk.co.nickthecoder.tickle.editor.util.ExtensionsKt

Resources.instance.textures.remove("fragments")
def managedTexture = new ManagedTexture( 300, 100 )

def alien = Resources.instance.poses.find("alien1")
def mask = FragmentMaker.randomMaskPose( alien, 4, 10 )

def fragmentMaker = new FragmentMaker(alien, mask)
fragmentMaker.texture = managedTexture
def fragments = fragmentMaker.generate()

def file = new File( Resources.instance.texturesDirectory, "fragments.png" )
FXCoderTab.saveImage(ExtensionsKt.toImage(managedTexture.texture), file)
managedTexture.texture.file = file

Resources.instance.textures.add( "fragments", managedTexture.texture )

def count = 1
for (fragment in fragments) {
    Resources.instance.poses.add( "alienFragment" + count, fragment.pose )
    count ++
}

managedTexture.texture


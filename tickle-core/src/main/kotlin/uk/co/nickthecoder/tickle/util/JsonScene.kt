/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.tickle.NoDirector
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextHAlignment
import uk.co.nickthecoder.tickle.graphics.TextVAlignment
import uk.co.nickthecoder.tickle.resources.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

open class JsonScene {

    val sceneResource: SceneResource

    constructor(file: File) {
        sceneResource = SceneResource()
        load(file)
    }

    constructor(sceneResource: SceneResource) {
        this.sceneResource = sceneResource
    }

    fun load(file: File) {
        sceneResource.file = Resources.instance.sceneDirectory.resolve(file)

        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()
        load(jroot)
    }

    protected open fun load(jroot: JsonObject) {

        sceneResource.directorString = jroot.getString("director", NoDirector::class.java.name)
        JsonUtil.loadAttributes(jroot, sceneResource.directorAttributes, "directorAttributes")

        sceneResource.layoutName = jroot.getString("layout", "default")
        sceneResource.background = Color.fromString(jroot.getString("background", "#FFFFFF"))
        sceneResource.showMouse = jroot.getBoolean("showMouse", true)



        jroot.get("include")?.let {
            val jincludes = it.asArray()
            jincludes.forEach { jinclude ->
                sceneResource.includes.add(Resources.instance.scenePathToFile(jinclude.asString()))
            }
        }

        sceneResource.layoutName
        jroot.get("stages")?.let {
            val jstages = it.asArray()
            jstages.forEach {
                loadStage(it.asObject())
            }
        }
    }


    fun loadStage(jstage: JsonObject) {
        val name = jstage.getString("name", "default")
        val stageResource = StageResource()
        sceneResource.stageResources[name] = stageResource

        jstage.get("actors")?.let {
            val jactors = it.asArray()
            jactors.forEach {
                loadActor(stageResource, it.asObject())
            }
        }
    }

    open protected fun createActorResource() = ActorResource()

    fun loadActor(stageResource: StageResource, jactor: JsonObject) {
        val actorResource = createActorResource()

        // NOTE. load the attributes FIRST, then change the costume, as that gives Attributes the chance to remove
        // attributes unsupported by the Role class.
        JsonUtil.loadAttributes(jactor, actorResource.attributes)
        actorResource.costumeName = jactor.get("costume").asString()
        val costume = Resources.instance.costumes.find(actorResource.costumeName)

        with(actorResource) {
            x = jactor.getDouble("x", 0.0)
            y = jactor.getDouble("y", 0.0)
            zOrder = jactor.getDouble("zOrder", costume?.zOrder ?: 0.0)

            // xAlignment was the old name. textAlignmentX was used by mistake. viewAlignment is the CORRECT name.
            viewAlignmentX = ActorXAlignment.valueOf(jactor.getString("viewAlignmentX", jactor.getString("xAlignment", "LEFT")))
            viewAlignmentY = ActorYAlignment.valueOf(jactor.getString("viewAlignmentY", jactor.getString("yAlignment", "BOTTOM")))

            direction.degrees = jactor.getDouble("direction", 0.0)

            // Legacy (when scale was a single Double, rather than a Vector2d)
            jactor.get("scale")?.let {
                scale.x = it.asDouble()
                scale.y = it.asDouble()
            }
            // This is the new form of scale.
            scale.x = jactor.getDouble("scaleX", 1.0)
            scale.y = jactor.getDouble("scaleY", 1.0)

            text = jactor.getString("text", "")
            textStyle?.let { textStyle ->
                jactor.get("textColor")?.let { textStyle.color = Color.fromString(it.asString()) }
                jactor.get("textOutlineColor")?.let { textStyle.outlineColor = Color.fromString(it.asString()) }
                jactor.get("textHAlignment")?.let { textStyle.halignment = TextHAlignment.valueOf(it.asString()) }
                jactor.get("textVAlignment")?.let { textStyle.valignment = TextVAlignment.valueOf(it.asString()) }
                jactor.get("font")?.let {
                    Resources.instance.fontResources.find(it.asString())?.let { textStyle.fontResource = it }
                }
            }

            if (isSizable()) {
                val rect = costume?.chooseNinePatch(costume.initialEventName)?.pose?.rect ?: costume?.choosePose(costume.initialEventName)?.rect
                size.x = jactor.getDouble("sizeX", rect?.width?.toDouble() ?: 1.0)
                size.y = jactor.getDouble("sizeY", rect?.height?.toDouble() ?: 1.0)
                sizeAlignment.x = jactor.getDouble("alignmentX", 0.5)
                sizeAlignment.y = jactor.getDouble("alignmentY", 0.5)
            }

        }

        stageResource.actorResources.add(actorResource)
    }

}

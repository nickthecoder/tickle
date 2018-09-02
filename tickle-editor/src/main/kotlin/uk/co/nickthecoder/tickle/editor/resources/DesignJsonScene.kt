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
package uk.co.nickthecoder.tickle.editor.resources

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.tickle.resources.ActorXAlignment
import uk.co.nickthecoder.tickle.resources.ActorYAlignment
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.StageResource
import uk.co.nickthecoder.tickle.util.JsonScene
import uk.co.nickthecoder.tickle.util.JsonUtil
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class DesignJsonScene : JsonScene {

    constructor(scene: DesignSceneResource) : super(scene)

    constructor(file: File) : super(DesignSceneResource()) {
        load(file)
    }

    override fun createActorResource() = DesignActorResource()

    override fun load(jroot: JsonObject) {
        super.load(jroot)

        sceneResource as DesignSceneResource // Ensure it is the correct sub-class

        jroot.get("snapToGrid")?.let {
            val jgrid = it.asObject()

            with(sceneResource.snapToGrid) {
                enabled = jgrid.getBoolean("enabled", false)

                spacing.x = jgrid.getDouble("xSpacing", 50.0)
                spacing.y = jgrid.getDouble("ySpacing", 50.0)

                offset.x = jgrid.getDouble("xOffset", 0.0)
                offset.y = jgrid.getDouble("yOffset", 0.0)

                closeness.x = jgrid.getDouble("xCloseness", 10.0)
                closeness.y = jgrid.getDouble("yCloseness", 10.0)
            }
            //println("Loaded grid ${sceneResource.grid}")
        }

        jroot.get("snapToGuides")?.let {
            val jguides = it.asObject()
            with(sceneResource.snapToGuides) {
                enabled = jguides.getBoolean("enabled", true)
                closeness = jguides.getDouble("closeness", 10.0)

                jguides.get("x")?.let {
                    val jx = it.asArray()
                    jx.forEach { xGuides.add(it.asDouble()) }
                }
                jguides.get("y")?.let {
                    val jy = it.asArray()
                    jy.forEach { yGuides.add(it.asDouble()) }
                }
            }
            //println("Loaded guides ${sceneResource.guides}")
        }

        jroot.get("snapToOthers")?.let {
            val jothers = it.asObject()
            with(sceneResource.snapToOthers) {
                enabled = jothers.getBoolean("enabled", true)
                closeness.x = jothers.getDouble("xCloseness", 10.0)
                closeness.y = jothers.getDouble("yCloseness", 10.0)
            }

        }

        jroot.get("snapRotation")?.let {
            val jrotation = it.asObject()
            with(sceneResource.snapRotation) {
                enabled = jrotation.getBoolean("enabled", true)
                stepDegrees = jrotation.getDouble("step", 15.0)
                closeness = jrotation.getDouble("closeness", 15.0)
            }

        }
    }

    fun save(file: File) {
        sceneResource.file = Resources.instance.sceneDirectory.resolve(file)

        val jroot = JsonObject()
        jroot.add("director", sceneResource.directorString)
        JsonUtil.saveAttributes(jroot, sceneResource.directorAttributes, "directorAttributes")

        jroot.add("background", sceneResource.background.toHashRGB())
        jroot.add("showMouse", sceneResource.showMouse)

        jroot.add("layout", sceneResource.layoutName)

        val jgrid = JsonObject()
        jroot.add("snapToGrid", jgrid)

        with((sceneResource as DesignSceneResource).snapToGrid) {
            jgrid.add("enabled", enabled == true)

            jgrid.add("xSpacing", spacing.x)
            jgrid.add("ySpacing", spacing.y)

            jgrid.add("xOffset", offset.x)
            jgrid.add("yOffset", offset.y)

            jgrid.add("xCloseness", closeness.x)
            jgrid.add("yCloseness", closeness.y)
        }

        val jguides = JsonObject()
        jroot.add("snapToGuides", jguides)
        with(sceneResource.snapToGuides) {
            jguides.add("enabled", enabled)
            jguides.add("closeness", closeness)

            val jx = JsonArray()
            xGuides.forEach { jx.add(it) }
            jguides.add("x", jx)

            val jy = JsonArray()
            yGuides.forEach { jy.add(it) }
            jguides.add("y", jy)
        }

        val jothers = JsonObject()
        jroot.add("snapToOthers", jothers)
        with(sceneResource.snapToOthers) {
            jothers.add("enabled", enabled)

            jothers.add("xCloseness", closeness.x)
            jothers.add("yCloseness", closeness.y)
        }

        val jrotation = JsonObject()
        jroot.add("snapRotation", jrotation)
        with(sceneResource.snapRotation) {
            jrotation.add("enabled", enabled)
            jrotation.add("step", stepDegrees)
            jrotation.add("closeness", closeness)
        }

        val jincludes = JsonArray()
        jroot.add("include", jincludes)
        sceneResource.includes.forEach { include ->
            jincludes.add(Resources.instance.sceneFileToPath(include))
        }

        val jstages = JsonArray()
        jroot.add("stages", jstages)
        sceneResource.stageResources.forEach { stageName, stageResource ->
            jstages.add(saveStage(stageName, stageResource))
        }

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, Resources.instance.preferences.outputFormat.writerConfig)
        }
    }

    fun saveStage(stageName: String, stageResource: StageResource): JsonObject {
        val jstage = JsonObject()
        jstage.add("name", stageName)
        val jactors = JsonArray()
        jstage.add("actors", jactors)

        stageResource.actorResources.forEach { actorResource ->
            val costume = Resources.instance.costumes.find(actorResource.costumeName)

            val jactor = JsonObject()
            jactors.add(jactor)
            with(actorResource) {
                jactor.add("costume", costumeName)
                jactor.add("x", x)
                jactor.add("y", y)
                if (zOrder != costume?.zOrder) { // Only save zOrders which are NOT the default zOrder for the Costume.
                    jactor.add("zOrder", zOrder)
                }

                textStyle?.let { textStyle ->

                    jactor.add("text", text)
                    if (textStyle.fontResource != costumeTextStyle?.fontResource) {
                        jactor.add("font", Resources.instance.fontResources.findName(textStyle.fontResource))
                    }
                    if (textStyle.halignment != costumeTextStyle?.halignment) {
                        jactor.add("textHAlignment", textStyle.halignment.name)
                    }
                    if (textStyle.valignment != costumeTextStyle?.valignment) {
                        jactor.add("textVAlignment", textStyle.valignment.name)
                    }
                    if (textStyle.color != costumeTextStyle?.color) {
                        jactor.add("textColor", textStyle.color.toHashRGBA())
                    }
                    if (textStyle.outlineColor != null && textStyle.outlineColor != costumeTextStyle?.outlineColor) {
                        jactor.add("textOutlineColor", textStyle.outlineColor!!.toHashRGBA())
                    }
                }

                if (viewAlignmentX != ActorXAlignment.LEFT) {
                    jactor.add("viewAlignmentX", viewAlignmentX.name)
                }
                if (viewAlignmentY != ActorYAlignment.BOTTOM) {
                    jactor.add("viewAlignmentY", viewAlignmentY.name)
                }

                jactor.add("direction", direction.degrees)
                jactor.add("scaleX", scale.x)
                jactor.add("scaleY", scale.y)

                if (isSizable()) {
                    jactor.add("sizeX", size.x)
                    jactor.add("sizeY", size.y)
                    jactor.add("alignmentX", sizeAlignment.x)
                    jactor.add("alignmentY", sizeAlignment.y)
                }
            }

            JsonUtil.saveAttributes(jactor, actorResource.attributes)
        }
        return jstage
    }

}

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
import com.eclipsesource.json.PrettyPrint
import javafx.application.Platform
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.tabs.EditTab
import uk.co.nickthecoder.tickle.editor.util.ClassLister
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.ResourceMap
import uk.co.nickthecoder.tickle.stage.FlexHAlignment
import uk.co.nickthecoder.tickle.stage.FlexVAlignment
import uk.co.nickthecoder.tickle.util.JsonResources
import uk.co.nickthecoder.tickle.util.JsonUtil
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class DesignJsonResources : JsonResources {

    constructor(file: File) : super(DesignResources()) {
        this.resources.file = file
    }

    constructor(resources: DesignResources) : super(resources)

    override fun postLoad(jroot: JsonObject) {
        super.postLoad(jroot)
        // The MainWindow may not exist yet, so runLater, when it will exist.
        Platform.runLater {
            loadTabs(jroot)
        }
    }

    override fun loadPreferences(jpreferences: JsonObject) {
        super.loadPreferences(jpreferences)
        ClassLister.packages(resources.preferences.packages)
    }

    fun save(file: File) {

        resources.file = file.absoluteFile

        val jroot = JsonObject()
        jroot.add("info", saveInfo())
        jroot.add("preferences", savePreferences())
        addArray(jroot, "layouts", saveLayouts())
        addArray(jroot, "textures", saveTextures())
        addArray(jroot, "fonts", saveFonts())
        addArray(jroot, "poses", savePoses())
        addArray(jroot, "sounds", saveSounds())
        addArray(jroot, "costumeGroups", saveCostumeGroups())
        addArray(jroot, "costumes", saveCostumes(resources.costumes, false))
        addArray(jroot, "inputs", saveInputs())
        addArray(jroot, "tabs", saveTabs())

        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, resources.preferences.outputFormat.writerConfig)
        }
    }


    private fun savePreferences(): JsonObject {
        val jpreferences = JsonObject()
        with(resources.preferences) {

            jpreferences.add("outputFormat", outputFormat.name)

            val jpackages = JsonArray()
            packages.forEach {
                jpackages.add(it)
            }
            jpreferences.add("packages", jpackages)
            jpreferences.add("treeThumbnailSize", treeThumnailSize)
            jpreferences.add("costumePickerThumbnailSize", costumePickerThumbnailSize)
            jpreferences.add("isMaximized", isMaximized)
            jpreferences.add("windowWidth", windowWidth)
            jpreferences.add("windowHeight", windowHeight)
            jpreferences.add("apiURL", apiURL)
            return jpreferences
        }
    }


    private fun saveInfo(): JsonObject {
        val jinfo = JsonObject()

        with(resources.gameInfo) {
            jinfo.add("title", title)
            jinfo.add("id", id)
            jinfo.add("width", width)
            jinfo.add("height", height)
            jinfo.add("resizable", resizable)
            jinfo.add("fullScreen", fullScreen)

            jinfo.add("initialScene", resources.sceneFileToPath(initialScenePath))
            jinfo.add("testScene", resources.sceneFileToPath(testScenePath))

            jinfo.add("producer", producerString)
        }

        if (resources.gameInfo.physicsEngine) {
            val jphysics = JsonObject()
            jinfo.add("physics", jphysics)
            with(resources.gameInfo.physicsInfo) {
                jphysics.add("gravity_x", gravity.x)
                jphysics.add("gravity_y", gravity.y)
                jphysics.add("velocityIterations", velocityIterations)
                jphysics.add("positionIterations", positionIterations)
                jphysics.add("scale", scale)
                jphysics.add("filterGroups", filterGroupsString)
                jphysics.add("filterBits", filterBitsString)
            }

        }

        return jinfo
    }

    private fun saveLayouts(): JsonArray {
        val jlayouts = JsonArray()
        resources.layouts.items().forEach { name, layout ->
            val jlayout = JsonObject()
            jlayouts.add(jlayout)
            jlayout.add("name", name)

            val jstages = JsonArray()
            jlayout.add("stages", jstages)

            layout.layoutStages.forEach { stageName, layoutStage ->
                val jstage = JsonObject()
                jstages.add(jstage)

                jstage.add("name", stageName)
                if (layoutStage.isDefault) {
                    jstage.add("isDefault", true)
                }
                jstage.add("stage", layoutStage.stageString)
                jstage.add("constraint", layoutStage.stageConstraintString)
                JsonUtil.saveAttributes(jstage, layoutStage.constraintAttributes, "constraintAttributes")
            }

            val jviews = JsonArray()
            jlayout.add("views", jviews)

            layout.layoutViews.forEach { viewName, layoutView ->
                val jview = JsonObject()
                jviews.add(jview)

                jview.add("name", viewName)
                jview.add("view", layoutView.viewString)
                if (layoutView.stageName.isNotBlank()) {
                    jview.add("stage", layoutView.stageName)
                }
                jview.add("zOrder", layoutView.zOrder)

                with(layoutView.position) {
                    jview.add("hAlignment", hAlignment.name)
                    if (hAlignment == FlexHAlignment.MIDDLE) {
                        jview.add("hPosition", hPosition)
                    } else {
                        jview.add("leftRightMargin", leftRightMargin)
                    }
                    width?.let { jview.add("width", it) }
                    widthRatio?.let { jview.add("widthRatio", it) }

                    jview.add("vAlignment", vAlignment.name)
                    if (vAlignment == FlexVAlignment.MIDDLE) {
                        jview.add("vPosition", vPosition)
                    } else {
                        jview.add("topBottomMargin", topBottomMargin)
                    }
                    height?.let { jview.add("height", it) }
                    heightRatio?.let { jview.add("heightRatio", it) }
                }
            }
        }
        // println("Created jlayouts $jlayouts")
        return jlayouts
    }

    private fun saveTextures(): JsonArray {
        val jtextures = JsonArray()
        resources.textures.items().forEach { name, texture ->
            texture.file?.let { file ->
                val jtexture = JsonObject()
                jtexture.add("name", name)
                jtexture.add("file", resources.toPath(file))
                jtextures.add(jtexture)
            }
        }
        return jtextures
    }


    private fun savePoses(): JsonArray {
        val jposes = JsonArray()
        resources.poses.items().forEach { name, pose ->
            resources.textures.findName(pose.texture)?.let { textureName ->
                val jpose = JsonObject()
                jpose.add("name", name)
                jpose.add("texture", textureName)
                jpose.add("left", pose.rect.left)
                jpose.add("bottom", pose.rect.bottom)
                jpose.add("right", pose.rect.right)
                jpose.add("top", pose.rect.top)
                jpose.add("offsetX", pose.offsetX)
                jpose.add("offsetY", pose.offsetY)
                jpose.add("direction", pose.direction.degrees)
                if (pose.tiled) {
                    jpose.add("tiled", true)
                }

                if (pose.snapPoints.isNotEmpty()) {
                    val jsnaps = JsonArray()
                    jpose.add("snapPoints", jsnaps)
                    pose.snapPoints.forEach { point ->
                        val jpoint = JsonObject()
                        jpoint.add("x", point.x)
                        jpoint.add("y", point.y)
                        jsnaps.add(jpoint)
                    }
                }

                jposes.add(jpose)
            }
        }
        return jposes

    }

    private fun saveCostumeGroups(): JsonArray {
        val jgroups = JsonArray()

        resources.costumeGroups.items().forEach { name, group ->
            val jgroup = JsonObject()
            jgroup.add("name", name)
            jgroup.add("costumes", saveCostumes(group, true))

            jgroup.add("showInSceneEditor", group.showInSceneEditor)
            jgroups.add(jgroup)
        }
        return jgroups
    }

    private fun saveCostumes(costumes: ResourceMap<Costume>, all: Boolean): JsonArray {
        val jcostumes = JsonArray()

        costumes.items().forEach { name, costume ->

            if (all || resources.findCostumeGroup(name) == null) {

                val jcostume = JsonObject()
                jcostume.add("name", name)
                jcostume.add("role", costume.roleString)
                jcostume.add("canRotate", costume.canRotate)
                jcostume.add("zOrder", costume.zOrder)
                jcostume.add("initialEvent", costume.initialEventName)
                jcostume.add("showInSceneEditor", costume.showInSceneEditor)
                if (costume.inheritEventsFrom != null) {
                    jcostume.add("inheritsEventsFrom", resources.costumes.findName(costume.inheritEventsFrom))
                }

                val jevents = JsonArray()
                jcostume.add("events", jevents)
                costume.events.forEach { eventName, event ->
                    val jevent = JsonObject()
                    jevents.add(jevent)
                    jevent.add("name", eventName)

                    if (event.poses.isNotEmpty()) {
                        val jposes = JsonArray()
                        event.poses.forEach { pose ->
                            resources.poses.findName(pose)?.let { poseName ->
                                jposes.add(poseName)
                            }
                        }
                        jevent.add("poses", jposes)
                    }

                    if (event.costumes.isNotEmpty()) {
                        val jcos = JsonArray()
                        event.costumes.forEach { cos ->
                            resources.costumes.findName(cos)?.let { cosName ->
                                jcos.add(cosName)
                            }
                        }
                        jevent.add("costumes", jcos)
                    }

                    if (event.textStyles.isNotEmpty()) {
                        val jtextStyles = JsonArray()
                        event.textStyles.forEach { textStyle ->
                            val jtextStyle = JsonObject()
                            jtextStyles.add(jtextStyle)
                            jtextStyle.add("font", resources.fontResources.findName(textStyle.fontResource))
                            jtextStyle.add("halign", textStyle.halignment.name)
                            jtextStyle.add("valign", textStyle.valignment.name)
                            jtextStyle.add("color", textStyle.color.toHashRGBA())
                            if (textStyle.fontResource.outlineFontTexture != null) {
                                textStyle.outlineColor?.let {
                                    jtextStyle.add("outlineColor", it.toHashRGBA())
                                }
                            }
                        }
                        jevent.add("textStyles", jtextStyles)
                    }

                    if (event.strings.isNotEmpty()) {
                        val jstrings = JsonArray()
                        event.strings.forEach { str ->
                            jstrings.add(str)
                        }
                        jevent.add("strings", jstrings)
                    }

                    if (event.sounds.isNotEmpty()) {
                        val jsounds = JsonArray()
                        event.sounds.forEach { sound ->
                            resources.sounds.findName(sound)?.let { soundName ->
                                jsounds.add(soundName)
                            }
                        }
                        jevent.add("sounds", jsounds)
                    }

                    if (event.ninePatches.isNotEmpty()) {
                        val jninePatches = JsonArray()
                        event.ninePatches.forEach { ninePatch ->
                            val jninePatch = JsonObject()
                            jninePatches.add(jninePatch)
                            jninePatch.add("pose", resources.poses.findName(ninePatch.pose))
                            jninePatch.add("left", ninePatch.left)
                            jninePatch.add("bottom", ninePatch.bottom)
                            jninePatch.add("right", ninePatch.right)
                            jninePatch.add("top", ninePatch.top)
                        }
                        jevent.add("ninePatches", jninePatches)
                    }
                }
                JsonUtil.saveAttributes(jcostume, costume.attributes)

                jcostumes.add(jcostume)
                costume.bodyDef?.let { saveBody(jcostume, it) }
            }
        }

        return jcostumes
    }

    private fun saveInputs(): JsonArray {
        val jinputs = JsonArray()
        resources.inputs.items().forEach { name, input ->
            val jinput = JsonObject()
            jinput.add("name", name)

            val jkeys = JsonArray()
            addKeyInputs(input, jkeys)
            if (!jkeys.isEmpty) {
                jinput.add("keys", jkeys)
            }

            val jmouseButtons = JsonArray()
            addMouseInputs(input, jmouseButtons)
            if (!jmouseButtons.isEmpty) {
                jinput.add("mouse", jmouseButtons)
            }

            val jjoystickButtons = JsonArray()
            addJoystickButtonInputs(input, jjoystickButtons)
            if (!jjoystickButtons.isEmpty) {
                jinput.add("joystick", jjoystickButtons)
            }

            val jjoystickAxis = JsonArray()
            addJoystickAxisInputs(input, jjoystickAxis)
            if (!jjoystickAxis.isEmpty) {
                jinput.add("joystickAxis", jjoystickAxis)
            }

            jinputs.add(jinput)
        }
        return jinputs
    }

    private fun addKeyInputs(input: Input, toArray: JsonArray) {

        if (input is KeyInput) {
            val jkey = JsonObject()
            jkey.add("key", input.key.label)
            jkey.add("state", input.state.name)
            toArray.add(jkey)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addKeyInputs(it, toArray)
            }
        }
    }

    private fun addJoystickButtonInputs(input: Input, toArray: JsonArray) {

        if (input is JoystickButtonInput) {
            val jjoystick = JsonObject()
            jjoystick.add("joystickID", input.joystickID)
            jjoystick.add("button", input.button.name)
            toArray.add(jjoystick)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addJoystickButtonInputs(it, toArray)
            }
        }
    }

    private fun addJoystickAxisInputs(input: Input, toArray: JsonArray) {

        if (input is JoystickAxisInput) {
            val jjoystick = JsonObject()
            jjoystick.add("joystickID", input.joystickID)
            jjoystick.add("axis", input.axis.name)
            jjoystick.add("positive", input.positive)
            jjoystick.add("threshold", input.threshold)
            toArray.add(jjoystick)

        } else if (input is CompoundInput) {
            input.inputs.forEach {
                addJoystickAxisInputs(it, toArray)
            }
        }
    }


    private fun saveFonts(): JsonArray {
        val jfonts = JsonArray()
        resources.fontResources.items().forEach { name, fontResource ->
            val jfont = JsonObject()
            jfont.add("name", name)
            if (fontResource.file == null) {
                jfont.add("fontName", fontResource.fontName)
                jfont.add("style", fontResource.style.name)
            } else {
                jfont.add("file", resources.toPath(fontResource.file!!))
            }
            fontResource.pngFile?.let {
                jfont.add("pngFile", resources.toPath(it))
            }
            jfont.add("size", fontResource.size)
            jfont.add("xPadding", fontResource.xPadding)
            jfont.add("yPadding", fontResource.yPadding)
            jfonts.add(jfont)
        }

        return jfonts

    }


    private fun saveSounds(): JsonArray {
        val jsounds = JsonArray()
        resources.sounds.items().forEach { name, sound ->
            sound.file?.let { file ->
                val jsound = JsonObject()
                jsound.add("name", name)
                jsound.add("file", resources.toPath(file))
                jsounds.add(jsound)
            }
        }
        return jsounds
    }

    private fun loadTabs(jroot: JsonObject) {
        val jtabs = jroot.get("tabs")
        if (jtabs is JsonArray) {
            for (jtab in jtabs) {
                if (jtab is JsonObject) {
                    val name = jtab.getString("name", null)
                    val type = jtab.getString("type", null)
                    if (name != null && type != null) {
                        val resourceType = ResourceType.valueOf(type)
                        MainWindow.instance.openNamedTab(name, resourceType)
                    }
                }
            }
        }
    }

    private fun saveTabs(): JsonArray {
        val jtabs = JsonArray()
        val tabs = MainWindow.instance.tabPane.tabs
        for (tab in tabs) {
            if (tab is EditTab) {
                val type = ResourceType.resourceType(tab.data)
                type?.let {
                    val jtab = JsonObject()
                    jtab.add("name", tab.dataName)
                    jtab.add("type", type.name)
                    jtabs.add(jtab)
                }
            }
        }
        return jtabs
    }

    companion object {

        fun saveFontMetrics(file: File, fontResource: FontResource) {
            val fontTexture = fontResource.fontTexture
            val jroot = JsonObject()
            jroot.add("lineHeight", fontTexture.lineHeight)
            jroot.add("leading", fontTexture.leading)
            jroot.add("ascent", fontTexture.ascent)
            jroot.add("descent", fontTexture.descent)
            jroot.add("xPadding", fontResource.xPadding)
            jroot.add("yPadding", fontResource.yPadding)
            val jglyphs = JsonArray()
            jroot.add("glyphs", jglyphs)
            fontTexture.glyphs.forEach { c, data ->
                val jglyph = JsonObject()
                jglyph.add("c", c.toString())
                jglyph.add("left", data.pose.rect.left)
                jglyph.add("top", data.pose.rect.top)
                jglyph.add("right", data.pose.rect.right)
                jglyph.add("bottom", data.pose.rect.bottom)
                jglyph.add("advance", data.advance)

                jglyphs.add(jglyph)
            }

            BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
                jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
            }
        }

    }
}

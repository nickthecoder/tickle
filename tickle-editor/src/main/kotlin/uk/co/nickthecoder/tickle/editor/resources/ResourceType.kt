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

import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.FXCoderStub
import uk.co.nickthecoder.tickle.editor.ScriptStub
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.sound.Sound
import java.io.File

enum class ResourceType(val label: String, val graphicName: String) {
    ANY("Resource", "folder2.png"),
    GAME_INFO("Game Info", "gameInfo.png"),
    PREFERENCES("Editor Preferences", "preferences.png"),
    API_Documentation("API Documentation", "api.png"),
    TEXTURE("Texture", "texture.png"),
    POSE("Pose", "pose.png"),
    COSTUME("Costume", "costume.png"),
    COSTUME_GROUP("Costume Group", "costumeGroup.png"),
    LAYOUT("Layout", "layout.png"),
    INPUT("Input", "input.png"),
    FONT("Font", "font.png"),
    SOUND("Sound", "sound.png"),
    SCENE_DIRECTORY("Scene Directory", "folder.png"),
    SCENE("Scene", "scene.png"),
    SCRIPT_DIRECTORY("Script Directory", "folder.png"),
    SCRIPT("Script", "script.png"),
    FXCODER_DIRECTORY("Script Directory", "folder.png"),
    FXCODER("FXCoder Script", "fxcoder.png");

    fun canCreate(): Boolean = this != ANY && this != GAME_INFO && this != PREFERENCES

    fun findResource(name: String): Any? {
        val resources = Resources.instance

        return when (this) {
            TEXTURE -> resources.textures.find(name)
            POSE -> resources.poses.find(name)
            COSTUME -> resources.costumes.find(name)
            COSTUME_GROUP -> resources.costumeGroups.find(name)
            LAYOUT -> resources.layouts.find(name)
            INPUT -> resources.inputs.find(name)
            FONT -> resources.fontResources.find(name)
            SOUND -> resources.sounds.find(name)
            SCENE -> {
                val file = resources.scenePathToFile(name)
                if (file.exists()) {
                    SceneStub(file)
                } else {
                    null
                }
            }
            SCRIPT -> {
                val file = File(Resources.instance.scriptDirectory(), name)
                if (file.exists()) {
                    ScriptStub(file)
                } else {
                    null
                }
            }
            FXCODER -> {
                val file = File(Resources.instance.fxcoderDirectory(), name)
                if (file.exists()) {
                    FXCoderStub(file)
                } else {
                    null
                }
            }
            else -> null
        }
    }

    companion object {

        fun resourceType(resource: Any): ResourceType? {
            return when (resource) {
                is GameInfo -> GAME_INFO
                is EditorPreferences -> PREFERENCES
                is Texture -> TEXTURE
                is Pose -> POSE
                is Costume -> COSTUME
                is CostumeGroup -> COSTUME_GROUP
                is Layout -> LAYOUT
                is Input -> INPUT
                is FontResource -> FONT
                is Sound -> SOUND
                is SceneResource -> SCENE
                is SceneStub -> SCENE
                is ScriptStub -> SCRIPT
                is FXCoderStub -> FXCODER
                else -> null
            }
        }
    }

}

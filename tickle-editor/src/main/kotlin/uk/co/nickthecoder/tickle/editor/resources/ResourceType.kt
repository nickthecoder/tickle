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

import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.CostumeGroup
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.ScriptStub
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Layout
import uk.co.nickthecoder.tickle.resources.SceneResource
import uk.co.nickthecoder.tickle.resources.SceneStub
import uk.co.nickthecoder.tickle.sound.Sound

enum class ResourceType(val label: String, val graphicName: String) {
    ANY("Resource", "folder2.png"),
    GAME_INFO("Game Info", "gameInfo.png"),
    PREFERENCES("Preferences", "preferences.png"),
    API_Documentation("API Documentation", "api.png"),
    TEXTURE("Texture", "texture.png"),
    POSE("Pose", "pose.png"),
    COSTUME("Costume", "costume.png"),
    COSTUME_GROUP("Costume Group", "folder2.png"),
    LAYOUT("Layout", "layout.png"),
    INPUT("Input", "input.png"),
    FONT("Font", "font.png"),
    SOUND("Sound", "sound.png"),
    SCENE_DIRECTORY("Scene Directory", "folder.png"),
    SCENE("Scene", "scene.png"),
    SCRIPT_DIRECTORY("Script Directory", "folder.png"),
    SCRIPT("Script", "script.png");

    fun canCreate(): Boolean = this != ANY && this != GAME_INFO && this != PREFERENCES

    companion object {

        fun resourceType(resource: Any): ResourceType? {
            return when (resource) {
                is GameInfo -> GAME_INFO
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
                else -> null
            }
        }
    }

}

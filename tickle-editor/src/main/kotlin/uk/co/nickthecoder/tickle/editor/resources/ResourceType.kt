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

enum class ResourceType(val label: String, val graphicName: String, val canCreate: Boolean = true) {
    ANY("Resource", "folder2.png"),
    GAME_INFO("Game Info", "gameInfo.png", canCreate = false),
    PREFERENCES("Preferences", "preferences.png", canCreate = false),
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
    SCRIPT("Script", "script.png");

    fun canCreate(): Boolean = this != ANY && this != GAME_INFO && this != PREFERENCES
}

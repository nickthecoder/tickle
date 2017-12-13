package uk.co.nickthecoder.tickle.editor.util

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
    SCENE("Scene", "scene.png");

    fun canCreate(): Boolean = this != ANY && this != GAME_INFO && this != PREFERENCES
}

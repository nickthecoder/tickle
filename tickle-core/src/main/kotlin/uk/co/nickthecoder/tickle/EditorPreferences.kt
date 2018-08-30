package uk.co.nickthecoder.tickle

import com.eclipsesource.json.WriterConfig

class EditorPreferences {

    var outputFormat: JsonFormat = JsonFormat.PRETTY

    val packages: MutableList<String> = mutableListOf("uk.co.nickthecoder.tickle")

    var treeThumnailSize: Int = 24

    var costumePickerThumbnailSize: Int = 40

    /**
     * The size of the scene used by the editor's MainWindow.
     * This allows the window to be the same size as the last time the editor was run.
     * Note, the size and isMaximized are NOT shown in the EditorPreferencesTask, and therefore
     * cannot be edited directly by the user.
     */
    var windowWidth = 1000.0

    var windowHeight = 600.0

    /**
     * Is the editor's MainWindow maximized? When true, the windowWidth and windowHeight are NOT
     * updated when the window closes
     */
    var isMaximized = false

    override fun toString(): String {
        return "outputFormat=$outputFormat packages=$packages"
    }

    enum class JsonFormat(val writerConfig: WriterConfig) {
        COMPACT(WriterConfig.MINIMAL),
        //UGLY(UglyPrint("  ")),
        PRETTY(WriterConfig.PRETTY_PRINT)
    }
}

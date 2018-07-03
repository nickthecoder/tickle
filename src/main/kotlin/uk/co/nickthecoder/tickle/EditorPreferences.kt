package uk.co.nickthecoder.tickle

import com.eclipsesource.json.WriterConfig

class EditorPreferences {

    var outputFormat: JsonFormat = JsonFormat.PRETTY

    val packages: MutableList<String> = mutableListOf("uk.co.nickthecoder.tickle")

    var treeThumnailSize: Int = 24

    var costumePickerThumbnailSize: Int = 40

    var windowWidth = 1000.0

    var windowHeight = 600.0

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

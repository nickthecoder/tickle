package uk.co.nickthecoder.tickle

import com.eclipsesource.json.UglyPrint
import com.eclipsesource.json.WriterConfig

class EditorPreferences {

    var outputFormat: JsonFormat = JsonFormat.PRETTY

    val packages: MutableList<String> = mutableListOf("uk.co.nickthecoder.tickle")

    var treeThumnailSize: Int = 24

    var costumePickerThumbnailSize: Int = 40

    var physicsEngine: Boolean = false

    override fun toString(): String {
        return "outputFormat=$outputFormat packages=$packages"
    }

    enum class JsonFormat(val writerConfig: WriterConfig) {
        COMPACT(WriterConfig.MINIMAL),
        UGLY(UglyPrint("  ")),
        PRETTY(WriterConfig.PRETTY_PRINT)
    }
}

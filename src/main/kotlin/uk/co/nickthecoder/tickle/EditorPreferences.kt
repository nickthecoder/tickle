package uk.co.nickthecoder.tickle

import com.eclipsesource.json.UglyPrint
import com.eclipsesource.json.WriterConfig

class EditorPreferences {

    var outputFormat: JsonFormat = JsonFormat.PRETTY

    val packages: MutableList<String> = mutableListOf("uk.co.nickthecoder.tickle")

    override fun toString(): String {
        return "outputFormat=$outputFormat packages=$packages"
    }

    enum class JsonFormat(val writerConfig: WriterConfig) {
        COMPACT(WriterConfig.MINIMAL),
        UGLY(UglyPrint("  ")),
        PRETTY(WriterConfig.PRETTY_PRINT)
    }
}

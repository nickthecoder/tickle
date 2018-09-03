package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import javafx.scene.web.WebView
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.resources.Resources

/**
 * Lists packages and classes which make up Tickle.
 * The tree is placed inside an [APIBox], which is in turn used by the [APITab].
 * Clicking on one of the package or classes in the list shows the appropriate API documentation
 * via the web (the API documentation is NOT stored locally).
 */
class APITree(val webView: WebView) : TreeView<String>() {

    private val baseUrl = Resources.instance.preferences.apiURL

    init {
        isEditable = false
        root = RootItem()
        root.children
        root.isExpanded = true
        selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue is SelectableTreeItem) {
                newValue.selected()
            }
        }
    }


    inner class RootItem : PackageItem("uk.co.nickthecoder.tickle") {

        val packageItems = mutableMapOf<String, PackageItem>(value to this)

        init {
            val reflections = Reflections("uk.co.nickthecoder.tickle", SubTypesScanner(false))
            reflections.configuration.scanners
            reflections.allTypes.forEach { className ->
                if (accept(className)) {
                    createClassItem(className)
                }
            }
            children.addAll(packageItems.values.filter { it != this }.sortedBy { it.value })
            packageItems.values.forEach { it.createChildren() }
        }

        fun accept(className: String): Boolean {
            // Ignore inner classes
            if (className.contains('$')) {
                return false
            }
            // Ignore classes from certain packages (which are of no use to game programmers).
            for (pack in excludePackages) {
                if (className.startsWith(pack)) {
                    return false
                }
            }
            return true
        }

        fun createClassItem(className: String) {
            val lastDot = className.lastIndexOf(".")
            val packageName = className.substring(0, lastDot)

            val simpleName = className.substring(lastDot + 1)
            var packageItem = packageItems[packageName]
            if (packageItem == null) {
                packageItem = PackageItem(packageName, packageName.replace(value + ".", ""))
                packageItems[packageName] = packageItem
            }
            packageItem.addClass(simpleName)

        }
    }


    abstract inner class SelectableTreeItem(name: String)

        : TreeItem<String>(name) {

        fun selected() {
            val url = baseUrl + (if (!baseUrl.endsWith('/')) "/" else "") + getURL()
            webView.engine.load(url)
        }

        abstract fun getURL(): String
    }

    open inner class PackageItem(val packageName: String, displayName: String = packageName)

        : SelectableTreeItem(displayName) {

        val classItems = mutableListOf<ClassItem>()

        init {
            graphic = ImageView(EditorAction.imageResource("folder.png"))
        }

        fun addClass(simpleName: String) {
            classItems.add(ClassItem(packageName, simpleName))
        }

        fun createChildren() {
            children.addAll(classItems.sortedBy { it.value })
        }

        override fun getURL() = "${encodeName(packageName)}/index.html"
    }

    inner class ClassItem(val packageName: String, val simpleName: String)

        : SelectableTreeItem(simpleName) {

        init {
            graphic = ImageView(EditorAction.imageResource("class.png"))

        }

        override fun getURL() = "${encodeName(packageName)}/${encodeName(simpleName)}/index.html"

    }

    companion object {
        val excludePackages = listOf(
                "uk.co.nickthecoder.tickle.editor",
                "uk.co.nickthecoder.tickle.groovy",
                "uk.co.nickthecoder.tickle.kotlin",
                "uk.co.nickthecoder.tickle.demo")
    }
}

private fun encodeName(name: String): String {
    val buffer = StringBuffer()
    name.forEach {
        if (it.isUpperCase()) {
            buffer.append("-").append(it.toLowerCase())
        } else {
            buffer.append(it)
        }
    }
    return buffer.toString()
}
package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.editor.EditorAction
import java.lang.reflect.Method

class ClassTree : TreeView<String>() {

    init {
        isEditable = false
        root = RootItem()
        isShowRoot = false
        root.children
        root.isExpanded = true
    }


    inner class RootItem() : TreeItem<String>() {

        init {
            addAll(Role::class.java, AbstractRole::class.java, Actor::class.java)
        }

        fun addAll(vararg klasses: Class<*>) {
            klasses.sortedBy { it.name }.forEach { add(it) }
        }

        fun add(klass: Class<*>) {
            val packag = klass.`package`
            createOrFindPackageItem(packag).add(klass)
        }

        fun createOrFindPackageItem(packag: Package): PackageItem {
            for (packageItem in children) {
                if (packageItem is PackageItem && packageItem.packag === packag) {
                    return packageItem
                }
            }
            val newPackage = PackageItem(packag)
            children.add(newPackage)
            return newPackage
        }
    }

    inner class PackageItem(val packag: Package) : TreeItem<String>(packag.name) {

        init {
            isExpanded = true
            graphic = ImageView(EditorAction.imageResource("folder.png"))
        }

        fun add(klass: Class<*>) {
            children.add(ClassItem(klass))
        }

    }

    inner class ClassItem(val klass: Class<*>) : TreeItem<String>(klass.simpleName) {

        init {
            graphic = ImageView(EditorAction.imageResource("class.png"))

            // TODO This does NOT include protected methods. Should it (trickier!)
            val methods = klass.methods
            val getMethodNames = methods.filter { it.name.startsWith("get") }.map { it.name }

            val propertyItems = mutableListOf<TreeItem<String>>()
            val methodItems = mutableListOf<TreeItem<String>>()

            methods.forEach { method ->
                if (!method.name.contains('$') && !IGNORE_METHOD_NAMES.contains(method.name)) {
                    if (method.name.matches(getterPattern)) {
                        // Show getters as properties
                        propertyItems.add(PropertyItem(method))
                    } else if (method.parameterCount == 1 && method.name.matches(setterPattern) && getMethodNames.contains(method.name.replaceFirst("set", "get"))) {
                        // Ignore setters
                    } else {
                        methodItems.add(MethodItem(method))
                    }
                }
            }
            children.addAll(propertyItems.sortedBy { it.value })
            children.addAll(methodItems.sortedBy { it.value })
        }

    }

    inner class MethodItem(val method: Method)
        : TreeItem<String>(label(method))

    inner class PropertyItem(val method: Method)
        : TreeItem<String>(
            method.name.substring(3, 4).toLowerCase() +
                    method.name.substring(4) +
                    " : " +
                    method.returnType.simpleName) {

        init {
            graphic = ImageView(EditorAction.imageResource("property.png"))
        }
    }

    companion object {
        val IGNORE_METHOD_NAMES = listOf("getClass", "equals", "hashCode", "notify", "notifyAll", "wait", "clone", "finalize")

        private val getterPattern = Regex("get[A-Z].*")
        private val setterPattern = Regex("set[A-Z].*")

    }

}

private fun label(method: Method): String {
    val buffer = StringBuffer()
    buffer.append(method.name).append("(")
    method.parameterTypes.forEach { pt ->
        buffer.append(pt.simpleName)
    }
    buffer.append(")")
    val returnType = method.returnType.simpleName
    if (returnType != "void") {
        buffer.append(" : $returnType")
    }
    return buffer.toString()
}

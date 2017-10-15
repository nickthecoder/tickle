package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.tickle.Attributes

object JsonUtil {

    fun loadAttributes(jparent: JsonObject, attributes: Attributes, tagName: String = "attributes") {
        jparent.get(tagName)?.let {
            it.asArray().forEach {
                val jattribute = it.asObject()
                val attributeName = jattribute.get("name").asString()
                val attributeValue = jattribute.get("value").asString()
                attributes.setValue(attributeName, attributeValue)
            }
        }
    }

    fun saveAttributes(jparent: JsonObject, attributes: Attributes, tagName: String = "attributes") {
        val map = attributes.map()
        if (map.isNotEmpty()) {
            val jattributes = JsonArray()

            map.forEach { name, data ->
                if (data.value != null) {
                    val jattribute = JsonObject()
                    jattribute.add("name", name)
                    jattribute.add("value", data.value)
                    jattributes.add(jattribute)
                }
            }
            if (!jattributes.isEmpty) {
                jparent.add(tagName, jattributes)
            }
        }
    }

}

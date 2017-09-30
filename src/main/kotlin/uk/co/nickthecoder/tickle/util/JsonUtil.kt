package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject

object JsonUtil {

    fun loadAttributes(jparent: JsonObject, attributes: MutableMap<String, String>) {
        jparent.get("attributes")?.let {
            it.asArray().forEach {
                val jattribute = it.asObject()
                val attributeName = jattribute.get("name").asString()
                val attributeValue = jattribute.get("value").asString()
                attributes[attributeName] = attributeValue
            }
        }
    }

    fun saveAttributes(jparent: JsonObject, attributes: MutableMap<String, String>) {
        if (attributes.isNotEmpty()) {
            val jattributes = JsonArray()
            jparent.add("attributes", jattributes)

            attributes.forEach { name, value ->
                val jattribute = JsonObject()
                jattribute.add("name", name)
                jattribute.add("value", value)
                jattributes.add(jattribute)
            }
        }
    }

}

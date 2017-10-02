package uk.co.nickthecoder.tickle.util

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.tickle.Attributes

object JsonUtil {

    fun loadAttributes(jparent: JsonObject, attributes: Attributes) {
        jparent.get("attributes")?.let {
            it.asArray().forEach {
                val jattribute = it.asObject()
                val attributeName = jattribute.get("name").asString()
                val attributeValue = jattribute.get("value").asString()
                attributes.map[attributeName] = attributeValue
            }
        }
    }

    fun saveAttributes(jparent: JsonObject, attributes: Attributes) {
        if (attributes.map.isNotEmpty()) {
            val jattributes = JsonArray()
            jparent.add("attributes", jattributes)

            attributes.map.forEach { name, value ->
                val jattribute = JsonObject()
                jattribute.add("name", name)
                jattribute.add("value", value)
                jattributes.add(jattribute)
            }
        }
    }

}

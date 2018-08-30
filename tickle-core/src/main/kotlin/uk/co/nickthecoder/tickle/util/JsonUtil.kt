/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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

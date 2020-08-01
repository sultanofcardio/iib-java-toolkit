package com.sultanofcardio.iib.util

import com.ibm.broker.plugin.MbElement
import com.ibm.broker.plugin.MbJSON
import org.json.JSONArray
import org.json.JSONObject

fun MbElement.populate(json: JSONArray) {
    val jsonElement: MbElement = if (parserClassName.equals(MbJSON.PARSER_NAME, ignoreCase = true)) {
        this
    } else {
        createElementAsLastChild(MbJSON.PARSER_NAME)
            .createElementAsLastChild(MbJSON.ARRAY, MbJSON.DATA_ELEMENT_NAME, null)
    }
    json.forEach {
        when (it) {
            is JSONObject -> {
                jsonElement.createElementAsLastChild(MbJSON.OBJECT, "Item", null)
                    .populate(json)
            }
            is JSONArray -> {
                jsonElement.createElementAsLastChild(MbJSON.ARRAY, "Item", null)
                    .populate(json)
            }
            else -> {
                jsonElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Item", it)
            }
        }
    }
}

fun MbElement.populate(json: JSONObject) {
    val jsonElement: MbElement = if (parserClassName.equals(MbJSON.PARSER_NAME, ignoreCase = true)) {
        this
    } else createElementAsLastChild(MbJSON.PARSER_NAME)
        .createElementAsLastChild(MbJSON.OBJECT, MbJSON.DATA_ELEMENT_NAME, null)
    json.keySet().map { it to json[it] }.forEach { (key, json) ->
        when (json) {
            is JSONObject -> {
                jsonElement.createElementAsLastChild(MbJSON.OBJECT, key, null)
                    .populate(json)
            }
            is JSONArray -> {
                jsonElement.createElementAsLastChild(MbJSON.ARRAY, key, null)
                    .populate(json)
            }
            else -> {
                jsonElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, key, json)
            }
        }
    }
}
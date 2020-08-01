package com.sultanofcardio.iib.nodes.processing

import com.ibm.broker.plugin.MbElement
import com.ibm.broker.plugin.MbException
import com.sultanofcardio.iib.errors.ProcessingError
import com.sultanofcardio.iib.models.JSONArrayMessage
import com.sultanofcardio.iib.models.JSONMessage
import com.sultanofcardio.iib.models.JSONObjectMessage
import com.sultanofcardio.iib.models.TextMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * A convenience class for processing JSON data.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class JSONProcessor protected constructor(configurableServiceName: String) :
    MessageProcessor(configurableServiceName) {

    protected lateinit var message: JSONMessage

    @Throws(MbException::class)
    override fun evaluate(inputRoot: MbElement) {
        try {
            val json = inputRoot.getFirstElementByPath("/JSON/Data")?.valueAsString
                ?: throw ProcessingError("Message is not JSON")
            message = createMessage(json)
            when (val m = message) {
                is JSONArrayMessage -> processArray(m)
                is JSONObjectMessage -> processObject(m)
            }
        } catch (e: ProcessingError) {
            handleError(e)
        } catch (t: Throwable) {
            handleError(ProcessingError("Error extracting message data from tree"))
        }
    }

    /**
     * Do what you need to with the [message], then route it to a terminal
     *
     * @param message The message to be processed
     */
    @Throws(ProcessingError::class)
    protected abstract fun processObject(message: JSONObjectMessage)

    /**
     * Do what you need to with the [message], then route it to a terminal
     *
     * @param message The message to be processed
     */
    @Throws(ProcessingError::class)
    protected abstract fun processArray(message: JSONArrayMessage)

    /**
     * Convert a string to a [JSONMessage]
     */
    protected open fun createMessage(json: String): JSONMessage {
        try {
            return when {
                json.startsWith("{") -> {
                    JSONObjectMessage(JSONObject(json), config)
                }
                json.startsWith("[") -> {
                    JSONArrayMessage(JSONArray(json), config)
                }
                else -> throw ProcessingError("Invalid JSON received")
            }
        } catch (e: JSONException) {
            throw ProcessingError("Invalid JSON received", TextMessage(json, config))
        }
    }
}

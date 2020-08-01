package com.sultanofcardio.iib.nodes.processing

import com.ibm.broker.javacompute.MbJavaComputeNode
import com.ibm.broker.plugin.MbElement
import com.ibm.broker.plugin.MbException
import com.ibm.broker.plugin.MbMessage
import com.ibm.broker.plugin.MbMessageAssembly
import com.sultanofcardio.iib.errors.ProcessingError
import com.sultanofcardio.iib.interfaces.Configuration
import com.sultanofcardio.iib.models.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A convenience class for processing raw text data.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class MessageProcessor protected constructor(@JvmField protected val config: Configuration) :
    MbJavaComputeNode() {

    protected lateinit var inAssembly: MbMessageAssembly

    protected constructor(configurableServiceName: String) : this(BrokerProxyConfig(configurableServiceName))

    @Throws(MbException::class)
    override fun evaluate(inAssembly: MbMessageAssembly) {
        try {
            this.inAssembly = inAssembly
            val inputRoot = this.inAssembly.message.rootElement
            evaluate(inputRoot)
        } catch (e: ProcessingError) {
            handleError(e)
        } catch (t: Throwable) {
            handleError(ProcessingError("Error extracting message data from tree"))
        }
    }

    @Throws(MbException::class)
    abstract fun evaluate(inputRoot: MbElement)

    /**
     * This method is called if a processing error occurs.
     *
     * @param e The processing error
     */
    protected open fun handleError(e: ProcessingError) {
        e.printStackTrace()
        val message = e.data
        if (message != null) {
            failure(message)
        } else {
            route("failure", inAssembly.message)
        }
    }

    fun out(message: Message) {
        route("out", message.toMbMessage())
    }

    fun alternate(message: Message) {
        route("alternate", message.toMbMessage())
    }

    fun failure(message: Message) {
        route("failure", message.toMbMessage())
    }

    protected fun route(terminal: String, message: MbMessage) {
        getOutputTerminal(terminal).propagate(MbMessageAssembly(inAssembly, message))
    }

    fun ByteArray.toMessage(): BlobMessage = BlobMessage(this, config)

    fun ByteArray.toMbMessage(): MbMessage = toMessage().toMbMessage()

    fun JSONObject.toMessage(): JSONObjectMessage = JSONObjectMessage(this, config)

    fun JSONObject.toMbMessage(): MbMessage = toMessage().toMbMessage()

    fun JSONArray.toMessage(): JSONArrayMessage = JSONArrayMessage(this, config)

    fun JSONArray.toMbMessage(): MbMessage = toMessage().toMbMessage()

    fun String.toMessage(): TextMessage = TextMessage(this, config)

    fun String.toMbMessage(): MbMessage = toMessage().toMbMessage()
}

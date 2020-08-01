package com.sultanofcardio.iib.nodes.processing

import com.ibm.broker.plugin.MbElement
import com.ibm.broker.plugin.MbException
import com.sultanofcardio.iib.errors.ProcessingError
import com.sultanofcardio.iib.models.TextMessage

/**
 * A convenience class for processing raw text data.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class TextProcessor protected constructor(configurableServiceName: String) :
    MessageProcessor(configurableServiceName) {

    protected lateinit var message: TextMessage

    @Throws(MbException::class)
    override fun evaluate(inputRoot: MbElement) {
        try {
            val text = inputRoot.lastChild?.lastChild?.valueAsString
                ?: throw ProcessingError("Message cannot be parsed to text")
            message = TextMessage(text, config)
            processText(message)
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
    protected abstract fun processText(message: TextMessage)
}

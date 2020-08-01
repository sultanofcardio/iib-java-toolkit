package com.sultanofcardio.iib.nodes.processing

import com.ibm.broker.plugin.MbElement
import com.ibm.broker.plugin.MbException
import com.sultanofcardio.iib.errors.ProcessingError
import com.sultanofcardio.iib.models.BlobMessage

/**
 * A convenience class for processing raw text data.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BlobProcessor protected constructor(configurableServiceName: String) :
    MessageProcessor(configurableServiceName) {

    protected lateinit var message: BlobMessage

    @Throws(MbException::class)
    override fun evaluate(inputRoot: MbElement) {
        try {
            val bytes = inputRoot.lastChild?.lastChild?.value as ByteArray?
                ?: throw ProcessingError("Message cannot be parsed as a byte array")
            message = BlobMessage(bytes, config)
            processBlob(message)
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
    protected abstract fun processBlob(message: BlobMessage)
}

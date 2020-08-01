package com.sultanofcardio.iib.models

import com.ibm.broker.plugin.MbBLOB
import com.ibm.broker.plugin.MbElement
import com.ibm.broker.plugin.MbMessage
import com.ibm.broker.plugin.MbXMLNSC
import com.sultanofcardio.iib.interfaces.Configuration
import com.sultanofcardio.iib.util.populate
import org.json.JSONArray
import org.json.JSONObject


/**
 * Represents a generic piece of data in the body of the logical message tree
 */
abstract class Message(val config: Configuration) {
    fun toMbMessage(): MbMessage {
        val outMessage = MbMessage()
        populate(outMessage.rootElement)
        return outMessage
    }

    protected abstract fun populate(element: MbElement)
}

/**
 * Represents blob data in the body of the logical message tree
 */
open class BlobMessage(val blob: ByteArray, config: Configuration) : Message(config) {
    override fun populate(element: MbElement) {
        val blobElement: MbElement = if (element.parserClassName.equals(MbBLOB.PARSER_NAME, ignoreCase = true)) {
            element
        } else {
            element.createElementAsLastChild(MbBLOB.PARSER_NAME)
        }

        blobElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", blob)
    }
}

open class TextMessage(val text: String, config: Configuration) : Message(config) {
    override fun populate(element: MbElement) {
        val blobElement: MbElement = if (element.parserClassName.equals(MbBLOB.PARSER_NAME, ignoreCase = true)) {
            element
        } else {
            element.createElementAsLastChild(MbBLOB.PARSER_NAME)
        }

        blobElement.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", text)
    }
}

/**
 * Represents a message that can be parsed into JSON using the JSON body parser
 */
sealed class JSONMessage(text: String, config: Configuration) : TextMessage(text, config)

/**
 * Represents a message that can be parsed into a JSON object using the JSON body parser
 */
open class JSONObjectMessage(
    val json: JSONObject,
    config: Configuration
) : JSONMessage(json.toString(), config) {
    override fun populate(element: MbElement) = element.populate(json)
}

/**
 * Represents a message that can be parsed into a JSON array using the JSON body parser
 */
open class JSONArrayMessage(
    val json: JSONArray,
    config: Configuration
) : JSONMessage(json.toString(), config) {
    override fun populate(element: MbElement) = element.populate(json)
}

/**
 * Represents a message that can be parsed using the XMLNSC body parser
 */
internal open class XMLMessage(val xml: String, config: Configuration) : TextMessage(xml, config) {
    override fun populate(element: MbElement) {

        val xmlElement: MbElement = if (element.parserClassName.equals(MbXMLNSC.PARSER_NAME, ignoreCase = true)) {
            element
        } else {
            element.createElementAsLastChild(MbXMLNSC.PARSER_NAME)
        }

        val xmlDecl: MbElement = xmlElement.createElementAsFirstChild(MbXMLNSC.XML_DECLARATION)
        xmlDecl.name = "XmlDeclaration"

        val version: MbElement = xmlDecl.createElementAsFirstChild(MbXMLNSC.ATTRIBUTE, "Version", "1.0")
        val encoding: MbElement = xmlDecl.createElementAsFirstChild(MbXMLNSC.ATTRIBUTE, "Encoding", "utf-8")
        val standalone: MbElement = xmlDecl.createElementAsFirstChild(MbXMLNSC.ATTRIBUTE, "Standalone", "yes")
    }
}

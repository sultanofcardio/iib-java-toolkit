package com.sultanofcardio.iib.util

import com.ibm.broker.plugin.*

class Router(private val inAssembly: MbMessageAssembly, private val node: MbNode) {
    @Throws(MbException::class)
    fun out(message: MbMessage) {
        route(node.getOutputTerminal("out"), message)
    }

    @Throws(MbException::class)
    fun alternate(message: MbMessage) {
        route(node.getOutputTerminal("alternate"), message)
    }

    @Throws(MbException::class)
    fun failure(message: MbMessage) {
        route(node.getOutputTerminal("failure"), message)
    }

    @Throws(MbException::class)
    fun route(terminal: MbOutputTerminal, outMessage: MbMessage) {
        val outAssembly = MbMessageAssembly(inAssembly, outMessage)
        terminal.propagate(outAssembly)
    }
}
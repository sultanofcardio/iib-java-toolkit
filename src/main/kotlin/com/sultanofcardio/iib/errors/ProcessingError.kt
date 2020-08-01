package com.sultanofcardio.iib.errors

import com.sultanofcardio.iib.models.Message

class ProcessingError(message: String, val data: Message? = null) : Exception(message)
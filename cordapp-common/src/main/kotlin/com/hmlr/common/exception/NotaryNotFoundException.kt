package com.hmlr.common.exception

import net.corda.core.CordaRuntimeException

class NotaryNotFoundException(override val message: String) : CordaRuntimeException(message)
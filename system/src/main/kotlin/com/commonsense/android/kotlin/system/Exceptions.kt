package com.commonsense.android.kotlin.system


/**
 * Exception describing people not reading the documentation,
 * and trying to use things wrongly, when no options is to disallowed bad usage.
 *
 */
class BadUsageException(message: String) : RuntimeException(message)


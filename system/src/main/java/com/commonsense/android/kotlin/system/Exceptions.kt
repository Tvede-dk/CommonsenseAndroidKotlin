package com.commonsense.android.kotlin.system

/**
 * Created by Kasper Tvede on 1/29/2018.
 * Purpose:
 * contains custom library exceptions.
 *
 */
/**
 * Exception describing people not reading the documentation,
 * and trying to use things wrongly, when no options is to disallowed bad usage.
 *
 */
class BadUsageException(message: String) : RuntimeException(message)
@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.compat

import java.lang.Exception
import java.security.*
import javax.net.ssl.*
import javax.net.ssl.TrustManagerFactory

/**
 * Created by kasper on 10/06/2017.
 *
 */
object TrustManagerFactory {
    fun getDefaultX509Trust(): X509TrustManager? = try {
        with(TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())) {
            init(null as KeyStore?)
            trustManagers?.firstOrNull() as? X509TrustManager
        }
    } catch (e: Exception) {
        null
    }
}


package com.commonsense.android.kotlin.base.compat

import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertLargerThan
import org.junit.Test
import javax.net.ssl.SSLSocket

/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 */
internal class SSLSocketFactoryCompatTest {

    private val socketFactory: SSLSocketFactoryCompat
        get() = SSLSocketFactoryCompat()

    @Test
    fun getSupportedCipherSuites() {
        socketFactory.defaultCipherSuites.size.assertLargerThan(0)
    }

    @Test
    fun getDefaultCipherSuites() {
        socketFactory.defaultCipherSuites.size.assertLargerThan(0)
    }

    @Test
    fun createSocket() {
        //TODO make me (requiring a server, and then doing the connections, validating on both ends that it is tls 1.2
        val fac = socketFactory
//        (fac.createSocket() as SSLSocket).enabledProtocols
//                .contains(SSLContextProtocols.TLSv12.algorithmName)
//                .assert(true, "should have tls 1.2 enabled")
//
//        (fac.createSocket("google.com",80) as SSLSocket).enabledProtocols
//                .contains(SSLContextProtocols.TLSv12.algorithmName)
//                .assert(true, "should have tls 1.2 enabled")

    }
}
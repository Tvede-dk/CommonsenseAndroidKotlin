package com.commonsense.android.kotlin.base.compat

import com.commonsense.android.kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 */
internal class SSLContextProtocolsTest {

    @Test
    fun createContext() {
        SSLContextProtocols.TLSv12.createContext().assertNotNull("tls1.2 should exist in jvm")
        SSLContextProtocols.TLSv11.createContext().assertNotNull("tls1.1 should exist in jvm")

    }

    @Test
    fun testalgorithmName() {
        SSLContextProtocols.SSL.algorithmName.contains("ssl", false)
        SSLContextProtocols.SSLv2.algorithmName.contains("ssl", false)
        SSLContextProtocols.SSLv3.algorithmName.contains("ssl", false)
        SSLContextProtocols.TLS.algorithmName.contains("tls", false)
        SSLContextProtocols.TLSv11.algorithmName.contains("tls", false)
        SSLContextProtocols.TLSv12.algorithmName.contains("tls", false)
    }

    @Test
    fun createSocketFactory() {
        SSLContextProtocols.TLSv12.createSocketFactory().assertNotNull("tls1.2 should exist in jvm")
        SSLContextProtocols.TLSv11.createSocketFactory().assertNotNull("tls1.1 should exist in jvm")
    }
}
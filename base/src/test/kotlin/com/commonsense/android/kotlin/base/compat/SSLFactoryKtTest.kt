package com.commonsense.android.kotlin.base.compat

import com.commonsense.android.kotlin.test.*
import org.junit.jupiter.api.*

/**
 *
 */
internal class SSLFactoryKtTest {


    @Test
    fun setProtocolToTls12() {
        val testSocket: TestSSLSocket = TestSSLSocket().setProtocolToTls12()
        testSocket.enabledProtocols.assertSize(1, "should only have tls 1.2")
        testSocket.enabledProtocols.first().assert(SSLContextProtocols.TLSv12.algorithmName)
    }
}
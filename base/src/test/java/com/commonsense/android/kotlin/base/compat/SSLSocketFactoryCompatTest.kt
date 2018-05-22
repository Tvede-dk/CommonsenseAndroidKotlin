package com.commonsense.android.kotlin.base.compat

import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertLargerThan
import com.commonsense.android.kotlin.test.assertSize
import org.junit.jupiter.api.Test
import javax.net.ssl.HandshakeCompletedListener
import javax.net.ssl.SSLSession
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

    @Test
    fun setProtocolToTls12() {
        val testSocket: TestSSLSocket = TestSSLSocket().setProtocolToTls12()
        testSocket.enabledProtocols.assertSize(1, "should only have tls 1.2")
        testSocket.enabledProtocols.first().assert(SSLContextProtocols.TLSv12.algorithmName)
    }

    class TestSSLSocket : SSLSocket() {

        private val internalProtocols = mutableListOf<String>()

        override fun setEnableSessionCreation(flag: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getNeedClientAuth(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getEnabledCipherSuites(): Array<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addHandshakeCompletedListener(listener: HandshakeCompletedListener?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setNeedClientAuth(need: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSupportedCipherSuites(): Array<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setWantClientAuth(want: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSupportedProtocols(): Array<String> {
            return internalProtocols.toTypedArray()
        }

        override fun startHandshake() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSession(): SSLSession {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setUseClientMode(mode: Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setEnabledProtocols(protocols: Array<out String>?) {
            val safeProtocols = protocols ?: return
            internalProtocols.clear()
            safeProtocols.forEach {
                internalProtocols.add(it)
            }
        }

        override fun setEnabledCipherSuites(suites: Array<out String>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removeHandshakeCompletedListener(listener: HandshakeCompletedListener?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getUseClientMode(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getEnableSessionCreation(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getEnabledProtocols(): Array<String> {
            return internalProtocols.toTypedArray()
        }

        override fun getWantClientAuth(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}
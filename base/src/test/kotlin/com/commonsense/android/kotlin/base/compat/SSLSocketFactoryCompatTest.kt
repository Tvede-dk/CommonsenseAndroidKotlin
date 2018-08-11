package com.commonsense.android.kotlin.base.compat

import com.commonsense.android.kotlin.test.*
import org.junit.jupiter.api.*
import java.net.*
import javax.net.ssl.*

/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 */
internal class SSLSocketFactoryCompatTest {

    private val socketFactory: SSLSocketFactoryCompat
        get() = SSLSocketFactoryCompat()

    @Test
    fun getSupportedCipherSuites() {
        socketFactory.supportedCipherSuites.size.assertLargerThan(0)
    }

    @Test
    fun getDefaultCipherSuites() {
        socketFactory.defaultCipherSuites.size.assertLargerThan(0)
    }

    @Test
    fun createSocket() {
        val fac = SSLSocketFactoryCompat(factory = MockedSSLFactory())
        fac.createSocket().assertTlsV12()
        fac.createSocket("google.com", 80).assertTlsV12()
        fac.createSocket("google.com",
                80,
                null,
                71).assertTlsV12()

        fac.createSocket("google.com",
                80,
                InetAddress.getLocalHost(),
                71).assertTlsV12()

        fac.createSocket(TestSSLSocket(),
                null,
                0,
                false).assertTlsV12()

        fac.createSocket(TestSSLSocket(),
                "",
                80,
                true).assertTlsV12()

        fac.createSocket(InetAddress.getLocalHost(),
                80).assertTlsV12()

        fac.createSocket(InetAddress.getLocalHost(),
                20,
                InetAddress.getLoopbackAddress(),
                21).assertTlsV12()
    }

    @Test
    fun setProtocolToTls12() {
        val testSocket: TestSSLSocket = TestSSLSocket().setProtocolToTls12()
        testSocket.enabledProtocols.assertSize(1, "should only have tls 1.2")
        testSocket.enabledProtocols.first().assert(SSLContextProtocols.TLSv12.algorithmName)
    }


}

private fun Socket.assertTlsV12() {
    (this as SSLSocket).enabledProtocols
            .contains(SSLContextProtocols.TLSv12.algorithmName)
            .assert(true, "should have tls 1.2 enabled")
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


class MockedSSLFactory : SSLSocketFactory() {

    override fun getDefaultCipherSuites(): Array<String> {
        return arrayOf()
    }

    override fun createSocket(): Socket {
        return TestSSLSocket()
    }

    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
        return TestSSLSocket()
    }

    override fun createSocket(host: String?, port: Int): Socket {
        return TestSSLSocket()
    }

    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket {
        return TestSSLSocket()
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        return TestSSLSocket()
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket {
        return TestSSLSocket()
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return arrayOf()
    }

}
